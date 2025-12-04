package com.kh.admin.notice.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.admin.notice.model.vo.AdminNoticeVO;
import com.kh.notice.model.dto.AttachmentDTO;

@Mapper
public interface AdminNoticeMapper {

	int countAll();

	List<NoticeAdminDTO> findAllByAdmin(Map<String, Object> params);

	void insertAttachment(AttachmentDTO at);

	void insertNotice(AdminNoticeVO adminNotice);

	NoticeAdminDTO findByNo(Long noticeNo);

	void delete(Long noticeNo);

	void updateNotice(AdminNoticeVO adminNotice);

	void resetAttachment(Long noticeNo);





}
