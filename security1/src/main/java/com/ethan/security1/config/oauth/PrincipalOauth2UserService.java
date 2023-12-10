package com.ethan.security1.config.oauth;

import com.ethan.security1.config.auth.PrincipalDetails;
import com.ethan.security1.config.oauth.provider.FacebookUserInfo;
import com.ethan.security1.config.oauth.provider.GoogleUserInfo;
import com.ethan.security1.config.oauth.provider.NaverUserInfo;
import com.ethan.security1.config.oauth.provider.OAuth2UserInfo;
import com.ethan.security1.model.User;
import com.ethan.security1.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    BCryptPasswordEncoder bCryptPasswordEncoder;
    UserRepository userRepository;

    public PrincipalOauth2UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수.
    // 함수 종료시 @AuthenticationPrincipal 어느테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getClientRegistration={}", userRequest.getClientRegistration()); // registrationID로 어떤 oauth로 로그인 했는지 알 수 있음.
        log.info("getAccessToken.getTokenValue={}", userRequest.getAccessToken().getTokenValue());


        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code를 return (Oauth-client 라이브러리) -> 억세스 토큰 요청
        // userRequest 정보 -> loadUser함수 -> 구글로부터 회원프로필 받아줌
        log.info("super.loadUser(userRequest).getAttributes={}", oAuth2User.getAttributes());


        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }

        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // google_34234뭐시기
        String password = bCryptPasswordEncoder.encode("임의값");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEnity = userRepository.findByUsername(username);

        if(userEnity == null) {
            userEnity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEnity);
        }

        return new PrincipalDetails(userEnity, oAuth2User.getAttributes());
    }
}
