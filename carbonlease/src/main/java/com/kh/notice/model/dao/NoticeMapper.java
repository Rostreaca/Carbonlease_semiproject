package com.kh.notice.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.dto.EventDTO;
import com.kh.notice.model.dto.NoticeDTO;

@Mapper
public interface NoticeMapper {

	int findAndCountAll();

	List<NoticeDTO> findAll(Map<String, Object> params);

	NoticeDTO findByNo(Long noticeNo);

	List<AttachmentDTO> getAttachment(Long noticeNo);

	void addViewCount(Long noticeNo);

	List<EventDTO> findAllEvents();

}
