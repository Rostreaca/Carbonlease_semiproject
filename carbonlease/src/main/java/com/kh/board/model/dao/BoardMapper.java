package com.kh.board.model.dao;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;

@Mapper
public interface BoardMapper {

	
	@Select("""
			SELECT
				   BOARD_NO boardNo
				 , BOARD_TITLE boardTitle
				 , BOARD_CONTENT boardContent
				 , VIEW_COUNT viewCount
				 , ENROLL_DATE enrollDate
				 , STATUS
				 , MEMBER_NO memberNo
				 , REGION_NO regionNo
			FROM
				   CL_BOARD
			JOIN
			INTO
				   
			""")
	
	List<BoardDTO> boardReadList();
	
	
	@Select("""
				SELECT
						REPLY_NO replyNo
					  , REPLY_CONTENT replyContent
					  , ENROLL_DATE enrollDate
					  , STATUS 
					  , BOARD_NO boardNo
					  , MEMBER_NO memberNo
				FROM
					    CL_BOARD_REPLY
			""")
	
	List<BoardReplyDTO> boardReplyList();
	
	
}
