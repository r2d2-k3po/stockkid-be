package net.stockkid.stockkidbe.security.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.TokensDTO;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class TokenUtil {

    @Autowired
    private JwtUtil jwtUtil;

    private final MemberService memberService;

    public TokensDTO generateTokens(String sub, String rol, String soc) throws Exception {

        TokensDTO tokensDTO = new TokensDTO();
        tokensDTO.setAccessToken(jwtUtil.generateAccessToken(sub, rol));
        tokensDTO.setRefreshToken(jwtUtil.generateRefreshToken(sub, rol, soc));

        log.info("successful accessToken : " + tokensDTO.getAccessToken());
        log.info("successful refreshToken : " + tokensDTO.getRefreshToken());

        memberService.updateRefreshToken(sub, tokensDTO.getRefreshToken());

        return tokensDTO;
    }

    public TokensDTO rotateTokens(String sub, String rol, String soc, String refreshToken) throws Exception {

        String refreshTokenDB = memberService.loadRefreshTokenByUsername(sub);

        if (Objects.equals(refreshToken, refreshTokenDB)) {
            return generateTokens(sub, rol, soc);

        } else {
            memberService.updateRefreshToken(sub,null);
            throw new Exception("refreshToken invalidated");
        }
    }
}
