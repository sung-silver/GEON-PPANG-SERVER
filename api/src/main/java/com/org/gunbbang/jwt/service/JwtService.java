package com.org.gunbbang.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.org.gunbbang.NotFoundException;
import com.org.gunbbang.errorType.ErrorType;
import com.org.gunbbang.repository.MemberRepository;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

  @Value("${jwt.secretKey}")
  private String secretKey;

  @Value("${jwt.access.expiration}")
  private Long accessTokenExpirationPeriod;

  @Value("${jwt.refresh.expiration}")
  private Long refreshTokenExpirationPeriod;

  @Value("${jwt.access.header}")
  private String accessHeader;

  @Value("${jwt.refresh.header}")
  private String refreshHeader;

  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
  private static final String EMAIL_CLAIM = "email";
  private static final String MEMBER_ID_CLAIM = "memberId";
  private static final String BEARER_PREFIX = "Bearer ";

  private final MemberRepository memberRepository;

  public String createAccessToken(String email, Long memberId) {
    Date now = new Date();
    return JWT.create()
        .withSubject(ACCESS_TOKEN_SUBJECT) // jwt의 subject 지정 (AccessToken으로 지정)
        .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료시간 지정
        .withClaim(EMAIL_CLAIM, email)
        .withClaim(MEMBER_ID_CLAIM, memberId)
        .sign(Algorithm.HMAC512(secretKey));
  }

  // refreshToken 생성
  public String createRefreshToken() {
    Date now = new Date();
    return JWT.create()
        .withSubject(REFRESH_TOKEN_SUBJECT)
        .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
        .sign(Algorithm.HMAC512(secretKey));
  }

  /** accessToken 재발급 후 header에 넣어서 리턴 */
  public void sendAccessToken(HttpServletResponse response, String accessToken) {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setHeader(accessHeader, accessToken);
    log.info("토큰 재발급 완료: {}", accessToken);
  }

  /** accessToken, refreshToken 재발급 후 header에 넣어서 리턴 */
  public void sendAccessAndRefreshToken(
      HttpServletResponse response, String accessToken, String refreshToken) {
    response.setStatus(HttpServletResponse.SC_OK);
    setAccessTokenHeader(response, accessToken);
    setRefreshTokenHeader(response, refreshToken);
    log.info("accessToken, refreshToken 재설정 완료");
  }

  /** accessToken 헤더 설정 */
  public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader(accessHeader, accessToken);
  }

  /** refreshToken 헤더 설정 */
  public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
    response.setHeader(refreshHeader, refreshToken);
  }

  /** header에서 refreshToken 추출 토큰 앞에 붙은 Bearer 문자열 삭제 후 리턴 */
  public Optional<String> extractRefreshToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(refreshHeader))
        .filter(refreshToken -> refreshToken.startsWith(BEARER_PREFIX))
        .map(refreshToken -> refreshToken.replace(BEARER_PREFIX, ""));
  }

  /** header에서 accessToken 추출 토큰 앞에 붙은 Bearer 문자열 삭제 후 리턴 */
  public Optional<String> extractAccessToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(accessHeader))
        .filter(accessToken -> accessToken.startsWith(BEARER_PREFIX))
        .map(accessToken -> accessToken.replace(BEARER_PREFIX, ""));
  }

  public Optional<String> extractEmailClaim(String accessToken) {
    try {
      DecodedJWT decodedJWT = getVerifiedJWT(accessToken);
      return Optional.ofNullable(decodedJWT.getClaim(EMAIL_CLAIM).asString());
    } catch (Exception e) {
      log.error("엑세스 토큰이 유효하지 않습니다");
      return Optional.empty();
    }
  }

  public Optional<Long> extractMemberIdClaim(String accessToken) {
    try {
      DecodedJWT decodedJWT = getVerifiedJWT(accessToken);
      return Optional.ofNullable(decodedJWT.getClaim(MEMBER_ID_CLAIM).asLong());
    } catch (Exception e) {
      log.error("엑세스 토큰이 유효하지 않습니다");
      return Optional.empty();
    }
  }

  private DecodedJWT getVerifiedJWT(String jwtToken) {
    return JWT.require(Algorithm.HMAC512(secretKey)).build().verify(jwtToken);
  }

  public void updateRefreshToken(String email, String refreshToken) {
    memberRepository
        .findByEmail(email)
        .ifPresentOrElse(
            member -> member.updateRefreshToken(refreshToken),
            () -> new NotFoundException(ErrorType.NOT_FOUND_USER_EXCEPTION));
  }

  public boolean isTokenValid(String token) {
    try {
      getVerifiedJWT(token);
      return true;
    } catch (Exception e) {
      log.error("유효하지 않은 토큰 {}", e.getMessage());
      return false;
    }
  }
}
