package com.kh.common.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseData<T> {
	private String message;
	private Object data;
	private String success;
	private LocalDateTime localDateTime;

    private ResponseData(String message, Object data, String success, LocalDateTime localDateTime) {
        this.message = message;
        this.success = success;
        this.data = data;
        this.localDateTime = localDateTime;
    }
	
	
	// 조회, 수정, 삭제 등에서 별도의 메시지 없이 data만 반환할 때 사용.
	public static <T> ResponseEntity<ResponseData<T>> ok(Object data) {
		return ResponseEntity.ok(new ResponseData<T>(null, data, "요청 성공", LocalDateTime.now()));
	}
	
	//200 : 조회, 수정, 삭제 200 ok
	public static <T> ResponseEntity<ResponseData<T>> ok(Object data, String message){
		return ResponseEntity.ok(new ResponseData<T>(message, data, "요청 성공", LocalDateTime.now()));
	}
	
	//201 : 등록시 201 created
	public static <T> ResponseEntity<ResponseData<T>> created(Object data) {
		return ResponseEntity.status(HttpStatus.CREATED)
							.body(new ResponseData<T>("생성되었습니다.", data, "요청 성공", LocalDateTime.now()));
	}
	
	//실패 응답 _ 글로벌 핸들러에서 돌리 것
	public static <T> ResponseEntity<ResponseData<T>> badRequest(String message, HttpStatus status){
		return ResponseEntity.status(status).body(new ResponseData<T>(message, null, "요청 실패", LocalDateTime.now()));
	}
}