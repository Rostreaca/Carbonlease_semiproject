package com.kh.event.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.kh.event.model.dao.EventMapper;
import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventMessageDTO;
import com.kh.event.model.dto.EventParticipationCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest implements EventServiceTest {

	@Mock
	private EventMapper eventMapper;
	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@InjectMocks
	private EventServiceImpl eventService;

	// 1. 이벤트 참여 성공
	@Test
	@DisplayName("이벤트참여_성공")
	void participateAndNotify_이벤트참여_성공() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		EventCampaignDTO mockEvent = new EventCampaignDTO();
		mockEvent.setEventId(eventId);
		mockEvent.setCurrentParticipants(500);
		mockEvent.setParticipationRate(50.0);

		when(eventMapper.existsParticipation(command)).thenReturn(0);		// 중복 참여 통과
		when(eventMapper.insertParticipant(command)).thenReturn(1);			// 참여 정보 저장 통과
		when(eventMapper.increaseParticipant(eventId)).thenReturn(1);		// 참여자 수 증가 통과
		when(eventMapper.selectEventCount(eventId)).thenReturn(mockEvent);	// 업데이트된 이벤트 정보 조회 성공

		// when (실행)
		EventCampaignDTO result = eventService.participateAndNotify(eventId, memberNo);

		// then (검증)
		assertThat(result).isEqualTo(mockEvent);

		// WebSocket 메시지 검증
		ArgumentCaptor<EventMessageDTO> captor = ArgumentCaptor.forClass(EventMessageDTO.class);
		verify(messagingTemplate).convertAndSend(eq("/sub/event/main"), captor.capture()); // 경로 확인

		EventMessageDTO message = captor.getValue();
		assertThat(message.getEventId()).isEqualTo(eventId);
		assertThat(message.getCurrentParticipants()).isEqualTo(500);
		assertThat(message.getMemberNo()).isNull(); // 브로드캐스트 확인
	}

	// 2. 중복 참여 발생
	@Test
	@DisplayName("중복참여발생_예외")
	void participateAndNotify_중복참여_예외() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		// when (실행)
		when(eventMapper.existsParticipation(command)).thenReturn(1); 	// 중복 참여 발생

		// then (검증)
		assertThatThrownBy(() -> eventService.participateAndNotify(eventId, memberNo))
				.isInstanceOf(IllegalStateException.class).hasMessage("이미 참여한 이벤트입니다.");

	}

	// 3. 참여 정보 저장 실패
	@Test
	@DisplayName("참여정보저장_실패")
	void participateAndNotify_참여정보저장_실패() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		// when (실행)
		when(eventMapper.existsParticipation(command)).thenReturn(0); 	// 중복 참여 통과
		when(eventMapper.insertParticipant(command)).thenReturn(0); 	// 참여 정보 저장 실패

		// then (검증)
		assertThatThrownBy(() -> eventService.participateAndNotify(eventId, memberNo))
				.isInstanceOf(RuntimeException.class).hasMessage("참여 정보 저장에 실패했습니다.");
	}

	// 4. 참여자 수 증가 실패
	@Test
	@DisplayName("참여자수증가_실패")
	void participateAndNotify_참여자수증가_실패() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		// when (실행)
		when(eventMapper.existsParticipation(command)).thenReturn(0); 	// 중복 참여 통과
		when(eventMapper.insertParticipant(command)).thenReturn(1); 	// 참여 정보 저장 통과
		when(eventMapper.increaseParticipant(eventId)).thenReturn(0); 	// 참여자 수 증가 실패

		// then (검증)
		assertThatThrownBy(() -> eventService.participateAndNotify(eventId, memberNo))
				.isInstanceOf(RuntimeException.class).hasMessage("참여자 수 업데이트에 실패했습니다.");
	}

	// 5. 업데이트된 이벤트 정보조회 실패
	@Test
	@DisplayName("업데이트된이벤트정보조회_실패")
	void participateAndNotify_업데이트된정보조회_실패() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		// when (실행)
		when(eventMapper.existsParticipation(command)).thenReturn(0); 	// 중복 참여 통과
		when(eventMapper.insertParticipant(command)).thenReturn(1); 	// 참여 정보 저장 통과
		when(eventMapper.increaseParticipant(eventId)).thenReturn(1); 	// 참여자 수 증가 통과
		when(eventMapper.selectEventCount(eventId)).thenReturn(null); 	// 업데이트된 정보조회 실패

		// then (검증)
		assertThatThrownBy(() -> eventService.participateAndNotify(eventId, memberNo))
				.isInstanceOf(RuntimeException.class).hasMessage("이벤트 정보를 찾을 수 없습니다.");

	}

	
	// 1. 메인이벤트조회 성공
	@Test
	@DisplayName("메인이벤트조회_성공")
	void getMainEvent_메인이벤트조회_성공() {

		// given (준비)
		EventCampaignDTO event = new EventCampaignDTO();

		when(eventMapper.selectMainEvent()).thenReturn(event); 	// 메인 이벤트 존재

		// when (실행)
		EventCampaignDTO result = eventService.getMainEvent();

		// then (검증)
		assertThat(result).isEqualTo(event);

	}

	// 2. 메인이벤트없어조회 실패
	@Test
	@DisplayName("메인이벤트가없어조회_실패")
	void getMainEvent_이벤트없음_예외() {
		
		// given (준비)
		when(eventMapper.selectMainEvent()).thenReturn(null);	// 메인 이벤트 없음

		// then (검증)
		assertThatThrownBy(() -> eventService.getMainEvent()).isInstanceOf(RuntimeException.class)
				.hasMessage("메인 이벤트를 찾을 수 없습니다.");
		
	}

	// 1. 특정이벤트조회_및_참여여부_성공
	@Test
	@DisplayName("특정이벤트조회_및_참여여부_정상")
	void getEventWithParticipation_정상() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventCampaignDTO event = new EventCampaignDTO();

		when(eventMapper.selectEventCount(eventId)).thenReturn(event);	// 특정 이벤트 존재

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		when(eventMapper.existsParticipation(command)).thenReturn(1);	// 참여 이력 존재

		// when (실행)
		EventCampaignDTO result = eventService.getEventWithParticipation(eventId, memberNo);

		// then (검증)
		assertThat(result).isEqualTo(event);
		assertThat(result.isParticipated()).isTrue();
	}

	// 2. 특정이벤트없어 실패
	@Test
	@DisplayName("특정이벤트가없으면 실패")
	void getEventWithParticipation_이벤트없음_예외() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;
		
		when(eventMapper.selectEventCount(eventId)).thenReturn(null);	// 특정 이벤트 없음

		// then (검증)
		assertThatThrownBy(() -> eventService.getEventWithParticipation(eventId, memberNo))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("존재하지 않는 이벤트입니다.");
	}
	
	
	// 1. WebSocket이벤트 업데이트 알림 성공
	@Test
	@DisplayName("WebSocket참여메시지_정상반환")
	void participateAndReturnEventMessage_정상() {

		// given (준비)
		Long eventId = 1L;
		Long memberNo = 1L;

		EventParticipationCommand command = EventParticipationCommand.builder().eventId(eventId).memberNo(memberNo)
				.build();

		EventCampaignDTO mockEvent = new EventCampaignDTO();
		mockEvent.setEventId(eventId);
		mockEvent.setCurrentParticipants(500);
		mockEvent.setParticipationRate(50.0);

		when(eventMapper.existsParticipation(command)).thenReturn(0);		// 중복 참여 통과
		when(eventMapper.insertParticipant(command)).thenReturn(1);			// 참여 정보 저장 통과
		when(eventMapper.increaseParticipant(eventId)).thenReturn(1);		// 참여자 수 증가 통과
		when(eventMapper.selectEventCount(eventId)).thenReturn(mockEvent);	// 업데이트된 이벤트 정보 조회 성공

		// when (실행)
		EventCampaignDTO result = eventService.participateAndNotify(eventId, memberNo);

		// then (검증)
		assertThat(result).isEqualTo(mockEvent);

		// WebSocket 메시지 검증
		ArgumentCaptor<EventMessageDTO> captor = ArgumentCaptor.forClass(EventMessageDTO.class);
		verify(messagingTemplate).convertAndSend(eq("/sub/event/main"), captor.capture()); // 경로 확인

		EventMessageDTO message = captor.getValue();
		assertThat(message.getEventId()).isEqualTo(eventId);
		assertThat(message.getCurrentParticipants()).isEqualTo(500);
		assertThat(message.getMemberNo()).isNull(); // 브로드캐스트 확인
	}
	
	
	// 2. WebSocket 참여 메시지 memberNo == null 예외 발생
	@Test
	@DisplayName("WebSocket 참여 메시지에 memberNo가 null이면 예외 발생")
	void getEventWithParticipation_예외() {

		// given (준비)
		Long eventId = 1L;

		EventMessageDTO message = new EventMessageDTO(eventId, 0, 0, null);

		// then (검증)
		assertThatThrownBy(() -> eventService.participateAndReturnEventMessage(message))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("memberNo는 null일 수 없습니다.");
	}

}
