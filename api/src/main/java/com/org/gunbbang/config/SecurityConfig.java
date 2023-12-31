package com.org.gunbbang.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.gunbbang.jwt.filter.JwtAuthenticationProcessingFilter;
import com.org.gunbbang.jwt.service.JwtService;
import com.org.gunbbang.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.org.gunbbang.login.handler.LoginFailureHandler;
import com.org.gunbbang.login.handler.LoginSuccessHandler;
import com.org.gunbbang.login.service.CustomUserDetailsService;
import com.org.gunbbang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtService jwtService;
  private final MemberRepository memberRepository;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .formLogin()
        .disable() // FormLogin 사용안함
        .httpBasic()
        .disable() // httpBasic 사용안함
        .headers()
        .frameOptions()
        .disable()
        .and()

        // 세션 사용하지 않으므로 STATELESS로 설정
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()

        // == URL별 권한 관리 옵션 ==//
        .authorizeRequests()

        // 아이콘, css, js 관련
        // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능, h2-console에 접근 가능
        .antMatchers(
            "/",
            "/css/**",
            "/images/**",
            "/js/**",
            "/favicon.ico",
            "/h2-console/**",
            "/health",
            "/profile")
        .permitAll()
        .antMatchers("/auth/signup", "/validation/nickname", "/validation/email")
        .permitAll() // 회원가입, 닉네임 중복체크, 이메일 중복체크는은모든 리소스에 접근 가능
        .antMatchers("/profile", "/actuator/health")
        .permitAll()
        .anyRequest()
        .authenticated();

    // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
    // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter ->
    // CustomJsonUsernamePasswordAuthenticationFilter
    http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
    http.addFilterBefore(
        jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CustomJsonUsernamePasswordAuthenticationFilter
      customJsonUsernamePasswordAuthenticationFilter() {
    CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter =
        new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
    customJsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(
        authenticationManager());
    customJsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(
        loginSuccessHandler());
    customJsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(
        loginFailureHandler());
    return customJsonUsernamePasswordAuthenticationFilter;
  }

  /**
   * AuthenticationManager 설정 후 등록 PasswordEncoder를 사용하는 AuthenticationProvider 지정 FormLogin(기존 스프링
   * 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용, 유저네임과 비밀번호를 기반으로 사용 UserDetailsService는 커스텀
   * LoginService로 등록 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return
   * ProviderManager)
   */
  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(customUserDetailsService);
    return new ProviderManager(provider);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /** 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록 */
  @Bean
  public LoginSuccessHandler loginSuccessHandler() {
    return new LoginSuccessHandler(jwtService, memberRepository);
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }

  @Bean
  public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
    JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter =
        new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
    return jwtAuthenticationProcessingFilter;
  }
}
