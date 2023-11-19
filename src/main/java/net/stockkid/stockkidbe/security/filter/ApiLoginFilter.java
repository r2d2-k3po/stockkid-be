package net.stockkid.stockkidbe.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.AuthDTO;
import net.stockkid.stockkidbe.dto.ResponseDTO;
import net.stockkid.stockkidbe.dto.ResponseStatus;
import net.stockkid.stockkidbe.dto.TokensDTO;
import net.stockkid.stockkidbe.entity.MemberSocial;
import net.stockkid.stockkidbe.security.util.IoUtil;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final IoUtil ioUtil;
    private final MemberService memberService;

    public ApiLoginFilter(String defaultFilterProcessesUrl, IoUtil ioUtil, MemberService memberService) {
        super(defaultFilterProcessesUrl);
        this.ioUtil = ioUtil;
        this.memberService = memberService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        log.info("ApiLoginFilter------------------------");
        log.info("attemptAuthentication");

        AuthDTO authDTO = ioUtil.readRequestBody(request, AuthDTO.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword());

        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authResult) {

        log.info("successfulAuthentication : " + authResult);
        log.info(authResult.getPrincipal());

        String username = ((User) authResult.getPrincipal()).getUsername();
        //get GrantedAuthority from Collection and remove ROLE_ prefix
        String role = authResult.getAuthorities().iterator().next().getAuthority().substring(5);

        try {
            TokensDTO tokensDTO = memberService.generateTokens(null, username, role, MemberSocial.UP.name());

            response.setStatus(HttpServletResponse.SC_CREATED);
            ResponseDTO responseDTO = new ResponseDTO(ResponseStatus.LOGIN_OK, "Login OK", tokensDTO);

            ioUtil.writeResponseBody(response, responseDTO);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
