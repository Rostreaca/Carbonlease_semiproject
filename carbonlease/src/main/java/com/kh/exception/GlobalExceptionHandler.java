package com.kh.exception;

import java.security.InvalidParameterException;
// import java.util.HashMap;
// import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.w3c.dom.events.EventException;

import com.kh.common.dto.ResponseData;
import com.kh.exception.campaign.CampaignException;
import com.kh.exception.reply.ReplyAccessDeniedException;
import com.kh.exception.reply.ReplyException;

import lombok.extern.slf4j.Slf4j;
/*
	발생(...Exception) - 포착(Global...의 handle..) - 포장(ResponseData) - 전송(ResponseEntity)
*/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// private ResponseEntity<Map<String, String>> createResponseEntity(RuntimeException e, HttpStatus status){
	// 	Map<String, String> error = new HashMap();
	// 	error.put("error-message", e.getMessage());
	// 	return ResponseEntity.status(status).body(error);
	// }

	// 400 Bad Request
	@ExceptionHandler({
	    UserNotFoundException.class,
	    IdDuplicateException.class,
	    NickNameDuplicateException.class,
	    EmailDuplicateException.class,
	    InvalidValueException.class,
	    AdminBoardsException.class,
	    InvalidParameterException.class,
		CampaignException.class,
		ReplyException.class,
		EventException.class
	})
	public ResponseEntity<ResponseData<Void>> handleBadRequest(RuntimeException e) {
	    return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	// 401 Unauthorized (인증/권한 관련)
	@ExceptionHandler(CustomAuthenticationException.class)
	public ResponseEntity<ResponseData<Void>> handleAuth(CustomAuthenticationException e){
	    return ResponseData.badRequest(e.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	// 403 Forbidden (관리자 권한 관련)
	@ExceptionHandler({
		AccessDeniedException.class,
		ReplyAccessDeniedException.class
	})
	public ResponseEntity<ResponseData<Void>> handleForbidden(RuntimeException e){
	    return ResponseData.badRequest(e.getMessage(), HttpStatus.FORBIDDEN);
	}

}
