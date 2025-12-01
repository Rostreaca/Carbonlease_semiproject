package com.kh.board.model.dto;
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardReplyDTO {
	private int replyNo;
	private int memberNo;
	private String replyContent;
	private Date enrollDate;
	private char status;
<<<<<<< HEAD
	private String nickname;
}
=======
}
>>>>>>> 8062900e5d6084482dea5909534538dc35ffb687
