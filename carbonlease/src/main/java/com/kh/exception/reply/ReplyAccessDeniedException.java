package com.kh.exception.reply;

public class ReplyAccessDeniedException extends RuntimeException {
    public ReplyAccessDeniedException(String message) {
        super(message);
    }
}
