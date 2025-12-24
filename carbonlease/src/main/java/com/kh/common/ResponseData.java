package com.kh.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Builder
@AllArgsConstructor
@Getter
public class ResponseData<T> {

	private String message;
	private Object data;
	private String success;
	
	public static <T> ResponseEntity<ResponseData<T>> ok(Object data){
		return ResponseEntity.ok(new ResponseData(null, data, "요청 성공"));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> ok(Object data, String message){
		return ResponseEntity.ok(new ResponseData<T>(message, data, "요청 성공"));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> created(Object data){
		return ResponseEntity.status(HttpStatus.CREATED)
							 .body(new ResponseData<T>("생성되었습니다.",data, "요청 성공"));

	}
	
	// 실패 응답
	public static <T> ResponseEntity<ResponseData<T>> badStatus(String message, HttpStatus status){
		return ResponseEntity.status(status)
							 .body(new ResponseData<T>(message, null, "요청 실패"));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> badRequest(String message){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(new ResponseData<T>(message, null, "요청 실패"));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> unAuthorized(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				 .body(new ResponseData<T>("요청 권한이 없습니다.", null, "요청 실패"));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> forbidden(String message){
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				 .body(new ResponseData<T>(message, null, "요청 실패"));

	}
	
}
