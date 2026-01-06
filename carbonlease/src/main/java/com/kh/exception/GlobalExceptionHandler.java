package com.kh.exception;

import java.security.InvalidParameterException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.w3c.dom.events.EventException;

import com.kh.common.responseData.ResponseData;
import com.kh.exception.campaign.CampaignException;
import com.kh.exception.reply.ReplyAccessDeniedException;
import com.kh.exception.reply.ReplyException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
/*
	발생(...Exception) - 포착(Global...의 handle..) - 포장(ResponseData) - 전송(ResponseEntity)
*/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 400 Bad Request
	@ExceptionHandler({
		InvalidValueException.class,
		AdminBoardsException.class,
		InvalidParameterException.class,
		CampaignException.class,
		ReplyException.class,
		EventException.class
	})
	public ResponseEntity<ResponseData<Void>> handleBadRequest(RuntimeException e, HttpServletRequest request) {
	    return ResponseData.badRequest(
	        e.getMessage(),
	        e.getClass().getSimpleName(),
	        request.getRequestURI()
	    );
	}

	// 401 Unauthorized (인증/권한 관련)
	@ExceptionHandler(
		CustomAuthenticationException.class
	)
	public ResponseEntity<ResponseData<Void>> handleAuth(CustomAuthenticationException e, HttpServletRequest request){
	    return ResponseData.unauthorized(
	        e.getMessage(),
	        e.getClass().getSimpleName(),
	        request.getRequestURI()
	    );
	}

	// 403 Forbidden (관리자 권한 관련)
	@ExceptionHandler({
		AccessDeniedException.class,
		ReplyAccessDeniedException.class
	})
	public ResponseEntity<ResponseData<Void>> handleForbidden(RuntimeException e, HttpServletRequest request){
	    return ResponseData.forbidden(
	        e.getMessage(),
	        e.getClass().getSimpleName(),
	        request.getRequestURI()
	    );
	}

	// 404 Not Found
	@ExceptionHandler(
		ResourceNotFoundException.class
	)
	public ResponseEntity<ResponseData<Void>> handleNotFound(ResourceNotFoundException e, HttpServletRequest request){
		return ResponseData.notFound(
			e.getMessage(),
			e.getClass().getSimpleName(),
			request.getRequestURI()
		);
	}

	// 409 Conflict
	@ExceptionHandler({
		IdDuplicateException.class,
		NickNameDuplicateException.class,
		EmailDuplicateException.class
		// DuplicateResourceException.class
	})
	public ResponseEntity<ResponseData<Void>> handleConflict(RuntimeException e, HttpServletRequest request){
		return ResponseData.conflict(
			e.getMessage(),
			e.getClass().getSimpleName(),
			request.getRequestURI()
		);
	}

	// 500 Internal Server Error (예상치 못한 모든 예외)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseData<Void>> handleException(Exception e, HttpServletRequest request){
		return ResponseData.error(
			e.getMessage(),
			e.getClass().getSimpleName(),
			request.getRequestURI()
		);
	}

}
