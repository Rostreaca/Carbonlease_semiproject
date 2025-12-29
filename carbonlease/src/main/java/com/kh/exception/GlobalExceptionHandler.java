package com.kh.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.common.util.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ResponseData<Object>> createResponseEntity(Exception e, HttpStatus status){
		return ResponseData.error(e.getMessage(), status);
	}
	
	@ExceptionHandler(CustomAuthenticationException.class)
	public ResponseEntity<ResponseData<Object>> handleAuth(CustomAuthenticationException e){
		return createResponseEntity(e, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseData<Object>> handleUser(UserNotFoundException e){
		return createResponseEntity(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(IdDuplicateException.class)
	public ResponseEntity<ResponseData<Object>> handleDuplicateId(IdDuplicateException e){
		return createResponseEntity(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NickNameDuplicateException.class)
	public ResponseEntity<ResponseData<Object>> handleDuplicateNickName(NickNameDuplicateException e){
		return createResponseEntity(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(EmailDuplicateException.class)
	public ResponseEntity<ResponseData<Object>> handleDuplicateEmail(EmailDuplicateException e){
		return createResponseEntity(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidValueException.class)
	public ResponseEntity<ResponseData<Object>> handleInvalidValue(InvalidValueException e){
		return createResponseEntity(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseData<Map<String, String>>> handleMethodArgument(MethodArgumentNotValidException e) {

	    Map<String, String> errors = new HashMap<>();
	    e.getBindingResult().getFieldErrors().forEach(err ->
	        errors.put(err.getField(), err.getDefaultMessage())
	    );

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ResponseData<>(false, "요청 값이 올바르지 않습니다.", errors));
	}
	
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	public ResponseEntity<ResponseData<Object>> handleAccessDenied(org.springframework.security.access.AccessDeniedException e){
	    return createResponseEntity(e, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(AdminBoardsException.class)
	public ResponseEntity<ResponseData<Object>> handleAdminError(AdminBoardsException e) {
	    return createResponseEntity(e, HttpStatus.BAD_REQUEST);
	}

}
