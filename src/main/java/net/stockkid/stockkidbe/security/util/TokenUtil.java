package net.stockkid.stockkidbe.security.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.MemberDTO;
import net.stockkid.stockkidbe.dto.TokensDTO;
import net.stockkid.stockkidbe.service.MemberService;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class TokenUtil {

    private final JwtUtil jwtUtil;

    private final MemberService memberService;

    public TokensDTO generateTokens(Long sid, String sub, String rol, String soc) throws Exception {

        TokensDTO tokensDTO = new TokensDTO();

        if (sid == null) {
            sid = memberService.loadUserByUsername(sub).getId();
        }
        tokensDTO.setAccessToken(jwtUtil.generateAccessToken(sid, rol));
        tokensDTO.setRefreshToken(jwtUtil.generateRefreshToken(sub, rol, soc));

        log.info("successful accessToken : " + tokensDTO.getAccessToken());
        log.info("successful refreshToken : " + tokensDTO.getRefreshToken());

        memberService.updateRefreshToken(sid, tokensDTO.getRefreshToken());

        return tokensDTO;
    }

    public TokensDTO rotateTokens(String sub, String rol, String soc, String refreshToken) throws Exception {

        MemberDTO memberDTO = memberService.loadUserByUsername(sub);

        if (Objects.equals(refreshToken, memberDTO.getRefreshToken())) {
            return generateTokens(memberDTO.getId(), sub, rol, soc);
        } else {
            throw new Exception("refreshToken not valid");
        }
    }

    public void invalidateToken(String sub, String refreshToken) throws Exception {

        MemberDTO memberDTO = memberService.loadUserByUsername(sub);

        if (Objects.equals(refreshToken, memberDTO.getRefreshToken())) {
            memberService.updateRefreshToken(memberDTO.getId(), null);
        } else {
            throw new Exception("refreshToken not valid");
        }
    }

    public String generateRandomPassword(int length) {

        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }
        return password.toString();
    }
}
