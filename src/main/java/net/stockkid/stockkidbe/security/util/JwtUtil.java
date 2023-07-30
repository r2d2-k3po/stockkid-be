package net.stockkid.stockkidbe.security.util;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.JWTClaimsDTO;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JwtUtil {

    @Value("${jwt.base64EncodedSecretKey}")
    private String base64EncodedSecretKey;

    //get a key by using JWTTests.java and save it in application.properties
    public static String getBase64EncodedSecretKey() {
        try {
            String algorithm = "HmacSHA256";
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            SecretKey secretKey = keyGenerator.generateKey();

            if (secretKey != null) {
                return Base64.getEncoder().encodeToString(secretKey.getEncoded());
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SecretKey getSecretKeyFromProperties() {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateToken(String sub, String rol) throws Exception {

        long sevenDaysInMinutes = 60 * 24 * 7;
        SecretKey secretKey = getSecretKeyFromProperties();

        return Jwts.builder()
                .setSubject(sub)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(sevenDaysInMinutes).toInstant()))
//                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(180).toInstant()))
                .signWith(secretKey)
                .compact();
    }

    public JWTClaimsDTO verifyAndExtract(String tokenStr) throws Exception {

        SecretKey secretKey = getSecretKeyFromProperties();

        JWTClaimsDTO jwtClaimsDTO = new JWTClaimsDTO();
        Jwt<?, ?> jwt = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(tokenStr);

        log.info(jwt);
        log.info(jwt.getBody().getClass());

        DefaultClaims claims = (DefaultClaims) jwt.getBody();

        jwtClaimsDTO.setUsername(claims.getSubject());
        jwtClaimsDTO.setRole((String) claims.get("rol"));
        return jwtClaimsDTO;
    }
}