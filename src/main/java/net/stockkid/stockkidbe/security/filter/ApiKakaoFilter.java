package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.entity.MemberSocial;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class ApiKakaoFilter extends OncePerRequestFilter {

    private final AntPathRequestMatcher antPathRequestMatcher;
    private final IoUtil ioUtil;
    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiKakaoFilter---------------------");

            try {
                AuthcodeDTO authcodeDTO = ioUtil.readRequestBody(request, AuthcodeDTO.class);
                log.info("authcode: " + authcodeDTO.getAuthcode());
                log.info("nonce: " + authcodeDTO.getNonce());

                KakaoTokenDTO kakaoTokenDTO = ioUtil.getKakaoToken(authcodeDTO.getAuthcode());
                log.info(kakaoTokenDTO);

                String email =  jwtUtil.verifyAndExtractKakaoIdToken(kakaoTokenDTO.getId_token(), authcodeDTO.getNonce());
                log.info(email);

                MemberDTO memberDTO = memberService.findUserByUsername(email);

                if (memberDTO == null) {
                    MemberDTO newMemberDTO = new MemberDTO();
                    newMemberDTO.setUsername(email);
                    newMemberDTO.setPassword(ioUtil.generateRandomPassword(30));
                    newMemberDTO.setMemberSocial(MemberSocial.KKO);

                    log.info("create new Kakao user");
                    Long sid = memberService.createUser(newMemberDTO);

                    log.info("create tokens for new user");
                    TokensDTO tokensDTO = memberService.generateTokens(sid, email, MemberRole.USER.name(), MemberSocial.KKO.name());

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

                    ioUtil.writeResponseBody(response, responseDTO);
                    return;
                } else if (memberDTO.getMemberSocial() == MemberSocial.KKO) {
                    if (!memberDTO.isEnabled()) throw new Exception("User disabled");
                    if (!memberDTO.isAccountNonExpired()) throw new Exception("Account expired");
                    if (!memberDTO.isCredentialsNonExpired()) throw new Exception("Credential expired");
                    if (!memberDTO.isAccountNonLocked()) throw new Exception("Account locked");

                    if (new AntPathRequestMatcher("/api/kakao/member/signin").matches(request)) {
                        log.info("create tokens for existing user");
                        TokensDTO tokensDTO = memberService.generateTokens(memberDTO.getId(), email, memberDTO.getMemberRole().name(), MemberSocial.KKO.name());

                        response.setStatus(HttpServletResponse.SC_OK);
                        ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

                        ioUtil.writeResponseBody(response, responseDTO);
                    } else if (new AntPathRequestMatcher("/api/kakao/member/deleteAccount").matches(request)) {
                        log.info("disable existing Kakao user");
                        memberDTO.setEnabled(false);
                        memberService.disableSocialUser(memberDTO.getId());

                        response.setStatus(HttpServletResponse.SC_OK);

                        ResponseDTO responseDTO = new ResponseDTO();
                        responseDTO.setApiStatus(ResponseStatus.AC_DL_OK);
                        responseDTO.setApiMsg("AC_DL OK");

                        ioUtil.writeResponseBody(response, responseDTO);
                    }
                    return;
                } else {
                    throw new Exception("User already exists");
                }
            } catch (Exception error) {
                log.info(error.getMessage());

                response.setStatus(HttpServletResponse.SC_CONFLICT);

                ResponseDTO responseDTO = new ResponseDTO();
                if (new AntPathRequestMatcher("/api/kakao/member/signin").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.LOGIN_FAIL);
                } else if (new AntPathRequestMatcher("/api/kakao/member/deleteAccount").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.AC_DL_FAIL);
                }
                responseDTO.setApiMsg(error.getMessage());

                ioUtil.writeResponseBody(response, responseDTO);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }
}