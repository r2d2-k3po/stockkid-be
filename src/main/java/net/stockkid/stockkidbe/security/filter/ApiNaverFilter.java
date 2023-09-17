package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.*;
import net.stockkid.stockkidbe.entity.MemberRole;
import net.stockkid.stockkidbe.entity.MemberSocial;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.security.util.TokenUtil;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class ApiNaverFilter  extends OncePerRequestFilter {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private IoUtil ioUtil;

    private final AntPathRequestMatcher antPathRequestMatcher;

    private final MemberService memberService;

    public ApiNaverFilter(AntPathRequestMatcher antPathRequestMatcher, MemberService memberService) {
        this.antPathRequestMatcher = antPathRequestMatcher;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (antPathRequestMatcher.matches(request)) {
            log.info("ApiNaverFilter---------------------");

            try {
                AuthcodeDTO authcodeDTO = ioUtil.readRequestBody(request, AuthcodeDTO.class);
                log.info("authcode: " + authcodeDTO.getAuthcode());
                log.info("state: " + authcodeDTO.getState());

                NaverTokenDTO naverTokenDTO = ioUtil.getNaverToken(authcodeDTO);
                log.info(naverTokenDTO);

                NaverUserInfoDTO naverUserInfoDTO = ioUtil.getNaverUserInfo(naverTokenDTO.getAccess_token());
                log.info(naverUserInfoDTO);

                String email = naverUserInfoDTO.getEmail();

                MemberDTO memberDTO = memberService.loadUserByUsername(email);

                if (memberDTO == null) {
                    MemberDTO newMemberDTO = new MemberDTO();
                    newMemberDTO.setUsername(email);
                    newMemberDTO.setPassword(tokenUtil.generateRandomPassword(30));
                    newMemberDTO.setFromSocial(MemberSocial.NAV);

                    log.info("create new Naver user");
                    memberService.createUser(newMemberDTO);

                    log.info("create tokens for new user");
                    TokensDTO tokensDTO = tokenUtil.generateTokens(null, email, MemberRole.USER.name(), MemberSocial.NAV.name());

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

                    ioUtil.writeResponseBody(response, responseDTO);
                    return;
                } else if (memberDTO.getFromSocial() == MemberSocial.NAV) {
                    if (!memberDTO.isEnabled()) throw new Exception("User disabled");
                    if (!memberDTO.isAccountNonExpired()) throw new Exception("Account expired");
                    if (!memberDTO.isCredentialsNonExpired()) throw new Exception("Credential expired");
                    if (!memberDTO.isAccountNonLocked()) throw new Exception("Account locked");

                    if (new AntPathRequestMatcher("/api/naver/member/signin").matches(request)) {
                        log.info("create tokens for existing user");
                        TokensDTO tokensDTO = tokenUtil.generateTokens(memberDTO.getMemberId(), email, memberDTO.getMemberRole().name(), MemberSocial.NAV.name());

                        response.setStatus(HttpServletResponse.SC_OK);
                        ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

                        ioUtil.writeResponseBody(response, responseDTO);
                    } else if (new AntPathRequestMatcher("/api/naver/member/deleteAccount").matches(request)) {
                        log.info("disable existing Naver user");
                        memberDTO.setEnabled(false);
                        memberService.disableSocialUser(memberDTO.getMemberId());

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
                if (new AntPathRequestMatcher("/api/naver/member/signin").matches(request)) {
                    responseDTO.setApiStatus(ResponseStatus.LOGIN_FAIL);
                } else if (new AntPathRequestMatcher("/api/naver/member/deleteAccount").matches(request)) {
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
