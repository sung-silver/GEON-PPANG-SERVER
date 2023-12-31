package com.org.gunbbang.login.service;

import com.org.gunbbang.entity.Member;
import com.org.gunbbang.login.CustomUserDetails;
import com.org.gunbbang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** DaoAuthenticationProvider 로 인증처리를 하기 위해 필요한 커스텀 UserDetailsService 클래스 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
    Member member =
        memberRepository
            .findByEmail(userName)
            .orElseThrow(() -> new UsernameNotFoundException("해당 이메일이 존재하지 않습니다."));

    return new CustomUserDetails(
        member.getEmail(),
        member.getPassword(),
        member.getMemberId(),
        member.getMainPurpose(),
        member.getBreadType().getBreadTypeId(),
        member.getNutrientType().getNutrientTypeId(),
        member.getNickname());
  }
}
