package com.asdf.minilog.entity;

public enum Role {
    ROLE_ADMIN, // 모든 사용자에 대한 수정과 삭제 권한 가짐
    ROLE_AUTHOR // User 과 Article 에 관한 CRUD 와 다른 사용자에 관한 팔로우/언팔로우 권한을 가짐
}
