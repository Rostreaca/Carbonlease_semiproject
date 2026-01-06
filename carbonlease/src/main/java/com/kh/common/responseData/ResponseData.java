package com.kh.common.responseData;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseData<T> {
	private boolean success; // 성공/실패
	private T data; // 성공 시 데이터
	private String message; // 안내 메시지
	private String errorCode; // 실패 시 에러코드(선택)
	private String path; // 실패 시 요청 경로(선택)
	private LocalDateTime timestamp;

	// 성공 응답 (200 OK)
	public static <T> ResponseEntity<ResponseData<T>> ok(T data, String message) {
		return ResponseEntity.ok(
			ResponseData.<T>builder()
				.success(true)
				.data(data)
				.message(message)
				.timestamp(LocalDateTime.now())
				.build()
		);
	}

	// 성공 응답 (201 Created)
	public static <T> ResponseEntity<ResponseData<T>> created(T data, String message) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ResponseData.<T>builder()
				.success(true)
				.data(data)
				.message(message)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 성공 응답 (204 No Content)
	public static <T> ResponseEntity<ResponseData<T>> noContent(String message) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
			.body(ResponseData.<T>builder()
				.success(true)
				.data(null)
				.message(message)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 실패 응답 (400 Bad Request)
	public static <T> ResponseEntity<ResponseData<T>> badRequest(String message, String errorCode, String path) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ResponseData.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.errorCode(errorCode)
				.path(path)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 실패 응답 (401 Unauthorized)
	public static <T> ResponseEntity<ResponseData<T>> unauthorized(String message, String errorCode, String path) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(ResponseData.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.errorCode(errorCode)
				.path(path)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 실패 응답 (403 Forbidden)
	public static <T> ResponseEntity<ResponseData<T>> forbidden(String message, String errorCode, String path) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ResponseData.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.errorCode(errorCode)
				.path(path)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 실패 응답 (404 Not Found)
	public static <T> ResponseEntity<ResponseData<T>> notFound(String message, String errorCode, String path) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ResponseData.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.errorCode(errorCode)
				.path(path)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 실패 응답 (409 Conflict)
	public static <T> ResponseEntity<ResponseData<T>> conflict(String message, String errorCode, String path) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(ResponseData.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.errorCode(errorCode)
				.path(path)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}

	// 실패 응답 (500 Internal Server Error)
	public static <T> ResponseEntity<ResponseData<T>> error(String message, String errorCode, String path) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ResponseData.<T>builder()
				.success(false)
				.data(null)
				.message(message)
				.errorCode(errorCode)
				.path(path)
				.timestamp(LocalDateTime.now())
				.build()
			);
	}
}