package com.kh.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseData<T> {
	
	private boolean success;
	private String message;
	private T data;
	
	// 성공 응답
	public static <T> ResponseEntity<ResponseData<T>> ok (String msg, T data){
		return ResponseEntity.ok(
				new ResponseData<T>(true, msg, data));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> ok (T data){
		return ResponseEntity.ok(
				new ResponseData<T>(true, "요청 성공하였습니다.", data));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> created (){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ResponseData<T>(true, "성공적으로 등록이 완료됐습니다.", null));
	}
	
	public static <T> ResponseEntity<ResponseData<T>> updated (){
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ResponseData<T>(true, "성공적으로 수정이 완료됐습니다.", null));
	}

	// 실패 응답
	public static <T> ResponseEntity<ResponseData<T>> error(String msg, HttpStatus status){
		return ResponseEntity.status(status)
				.body(new ResponseData<T>(false, msg, null));
	}

}
