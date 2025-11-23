package com.kh.notice.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.notice.model.dto.NoticeDTO;

@Mapper
public interface NoticeMapper {

	int findAndCountAll();

	List<NoticeDTO> findAll(Map<String, Object> params);

	NoticeDTO findByNo(Long noticeNo);

}
