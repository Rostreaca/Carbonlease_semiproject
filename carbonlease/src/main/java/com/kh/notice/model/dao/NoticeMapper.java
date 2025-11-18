package com.kh.notice.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.notice.model.dto.NoticeDTO;

@Mapper
public interface NoticeMapper {

	List<NoticeDTO> findAll(RowBounds rb);

}
