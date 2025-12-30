package com.kh.exception;

import java.security.InvalidParameterException;
// import java.util.HashMap;
// import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.common.dto.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// private ResponseEntity<Map<String, String>> createResponseEntity(RuntimeException e, HttpStatus status){
	// 	Map<String, String> error = new HashMap();
	// 	error.put("error-message", e.getMessage());
	// 	return ResponseEntity.status(status).body(error);
	// }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseData<Void>> handleRuntimeException(RuntimeException e){
        log.error("RuntimeException 발생: ", e);
        return ResponseData.badRequest("서버 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
    // 인증 관련 예외 처리  
	@ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ResponseData<Void>> handleAuth(CustomAuthenticationException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 사용자 관련 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseData<Void>> handleUser(UserNotFoundException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 중복 예외 처리
    @ExceptionHandler(IdDuplicateException.class)
    public ResponseEntity<ResponseData<Void>> handleDuplicateId(IdDuplicateException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //  닉네임
    @ExceptionHandler(NickNameDuplicateException.class)
    public ResponseEntity<ResponseData<Void>> handleDuplicateNickName(NickNameDuplicateException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 이메일 중복 예외 처리
    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ResponseData<Void>> handleDuplicateEmail(EmailDuplicateException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 잘못된 값 예외 처리
    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<ResponseData<Void>> handleInvalidValue(InvalidValueException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 리소스 없음 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<Void>> handleMethodArgument(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldError() != null
            ? e.getBindingResult().getFieldError().getDefaultMessage()
            : "잘못된 요청입니다.";
        return ResponseData.badRequest(message, HttpStatus.BAD_REQUEST);
    }

    // 접근 권한 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseData<Void>> handleAccessDenied(AccessDeniedException e){
        return ResponseData.badRequest(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    //  관리자 게시판 예외 처리
    @ExceptionHandler(AdminBoardsException.class)
    public ResponseEntity<ResponseData<Void>> handleAdminError(AdminBoardsException e) {
        return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // 잘못된 파라미터 예외 처리
	@ExceptionHandler(InvalidParameterException.class)
	public ResponseEntity<ResponseData<Void>> handleInvalidParam(InvalidParameterException e) {
		return ResponseData.badRequest(e.getMessage(), HttpStatus.BAD_REQUEST);
	}	

}
