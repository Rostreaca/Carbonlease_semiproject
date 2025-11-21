package com.kh.notice.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.admin.notice.model.vo.AdminNoticeVO;
import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.dto.NoticeDTO;

import jakarta.validation.Valid;

@Mapper
public interface NoticeMapper {

	int findAndCountAll();

	List<NoticeDTO> findAll(Map<String, Object> params);

	NoticeDTO findByNo(Long noticeNo);

	int countAll();

	List<NoticeAdminDTO> findAllByAdmin(Map<String, Object> params);

	void insertNotice(AdminNoticeVO adminNotice);

	void insertAttachment(List<AttachmentDTO> ats);


}
