package com.asdf.minilog.exception;

// 인증 실패시 사용
public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
