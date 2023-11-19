package net.stockkid.stockkidbe.security.util;

import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.JWTClaimsDTO;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.*;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;

@Log4j2
public class JwtUtil {

    @Value("${jwk.json}")
    private String jwkJson;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.idtoken.iss}")
    private String kakaoIdTokenIss;

    private static final HttpsJwks httpsJkws = new HttpsJwks("https://kauth.kakao.com/.well-known/jwks.json");
    private static final HttpsJwksVerificationKeyResolver httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJkws);

    //get a key by using JWTTests.java and save it in application.properties
    public static String getRsaJsonWebKey() {
        try {
            RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
            rsaJsonWebKey.setKeyId("k1");
            return rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);
        } catch (JoseException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    private RsaJsonWebKey getJwkFromProperties() {
        try {
            return (RsaJsonWebKey) PublicJsonWebKey.Factory.newPublicJwk(jwkJson);
        } catch (JoseException e) {
            log.info(e.getMessage());
        }
        return null;
    }

    public String generateAccessToken(Long sid, String rol) throws Exception {

        long thirtyMinutes = 30;
        RsaJsonWebKey rsaJsonWebKey = getJwkFromProperties();

        JwtClaims claims = new JwtClaims();
        claims.setClaim("sid", sid);
        claims.setStringClaim("rol", rol);
        claims.setIssuedAtToNow();
        claims.setExpirationTimeMinutesInTheFuture(thirtyMinutes);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        assert rsaJsonWebKey != null;
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return jws.getCompactSerialization();
    }

    public String generateRefreshToken(String sub, String rol, String soc) throws Exception {

        long sevenDaysInMinutes = 7 * 24 * 60;
        RsaJsonWebKey rsaJsonWebKey = getJwkFromProperties();

        JwtClaims claims = new JwtClaims();
        claims.setSubject(sub);
        claims.setStringClaim("rol", rol);
        claims.setStringClaim("soc", soc);
        claims.setIssuedAtToNow();
        claims.setExpirationTimeMinutesInTheFuture(sevenDaysInMinutes);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        assert rsaJsonWebKey != null;
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return jws.getCompactSerialization();
    }

    public JWTClaimsDTO verifyAndExtractToken(String tokenStr) throws Exception {

        RsaJsonWebKey rsaJsonWebKey = getJwkFromProperties();

        assert rsaJsonWebKey != null;
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setVerificationKey(rsaJsonWebKey.getKey())
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256)
                .build();

        JwtClaims claims = jwtConsumer.processToClaims(tokenStr);

        JWTClaimsDTO jwtClaimsDTO = new JWTClaimsDTO();
        jwtClaimsDTO.setMemberId((Long) claims.getClaimValue("sid"));
        jwtClaimsDTO.setUsername(claims.getSubject());
        jwtClaimsDTO.setRole(claims.getStringClaimValue("rol"));
        jwtClaimsDTO.setSocial(claims.getStringClaimValue("soc"));
        return jwtClaimsDTO;
    }

    public String verifyAndExtractKakaoIdToken(String idTokenStr, String kakaoNonce) throws Exception {

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKeyResolver(httpsJwksKeyResolver)
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setExpectedIssuer(true, kakaoIdTokenIss)
                .setExpectedAudience(true, kakaoClientId)
                .build();

        JwtClaims claims = jwtConsumer.processToClaims(idTokenStr);

        if (!kakaoNonce.equals(claims.getStringClaimValue("nonce"))) throw new Exception("invalid nonce");

        String email = claims.getStringClaimValue("email");
        if (email == null) throw new Exception("invalid Kakao email");

        return email;
    }
}
