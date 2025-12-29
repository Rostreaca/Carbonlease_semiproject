package com.kh.notice.model.dto.response;

import java.util.List;

import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.dto.NoticeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NoticeDetailResponse {

	private NoticeDTO notice;
	private List<AttachmentDTO> attachments;
}
