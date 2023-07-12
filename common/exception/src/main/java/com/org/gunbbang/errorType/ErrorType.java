package com.org.gunbbang.errorType;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorType {
    /**
     * 400 BAD REQUEST
     */
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    INVALID_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다"),
    TOKEN_TIME_EXPIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "만료된 토큰입니다"),
    ALREADY_EXIST_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 등록된 이메일입니다"),
    ALREADY_EXIST_NICKNAME_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 등록된 닉네임입니다"),
    ALREADY_BOOKMARKED_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 북마크된 빵집입니다"),
    ALREADY_CANCELED_BOOKMARK_EXCEPTION(HttpStatus.BAD_REQUEST, "북마크되지 않은 건빵집의 북마크를 취소하려는 시도입니다"),

    /**
     * 404 NOT FOUND
     */
    NOT_FOUND_USER_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다"),
    NOT_FOUND_IMAGE_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 이미지입니다"),
    INVALID_MULTIPART_EXTENSION_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 이미지입니다"),
    NOT_FOUND_BAKERY_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 건빵집입니다"),
    NOT_FOUND_SAVE_REVIEW(HttpStatus.NOT_FOUND, "리뷰 저장에 실패했습니다"),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다"),
    NOT_FOUND_CATEGORY_EXCEPTION(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다"),

    /**
     * 409 CONFLICT
     */
    ALREADY_EXIST_USER_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 유저입니다"),


    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
