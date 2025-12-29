package com.kh.notice.model.dto.response;

import java.util.List;

import com.kh.common.util.PageInfo;
import com.kh.notice.model.dto.NoticeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticesListResponse {
	
	private List<NoticeDTO> notices;
	private PageInfo pageInfo;
}
