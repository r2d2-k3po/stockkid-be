package net.stockkid.stockkidbe.security;

import net.stockkid.stockkidbe.dto.JWTClaimsDTO;
import net.stockkid.stockkidbe.security.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTTests {

    @Autowired
    private JwtUtil jwtUtil;

    //get a key and save it in application.properties
    @Test
    public void generateRsaJsonWebKey() throws Exception {

        String rsaJsonWebKeyJson = JwtUtil.getRsaJsonWebKey();

        System.out.println("rsaJsonWebKeyJson : " + rsaJsonWebKeyJson);
    }

    @Test
    public void testEncode() throws Exception {
        String username = "jwtTestUser";
        String role = "USER";
        String social = "UP";

        String token = jwtUtil.generateRefreshToken(username, role, social);

        System.out.println("token : " + token);
    }

    @Test
    public void testVerify() throws Exception {

        String username = "jwtTestUser";
        String role = "USER";
        String social = "UP";
        String token = jwtUtil.generateRefreshToken(username, role, social);

        Thread.sleep(5000);

        JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtractToken(token);

        System.out.println("username : " + jwtClaimsDTO.getUsername());
        System.out.println("role : " + jwtClaimsDTO.getRole());
        System.out.println("social : " + jwtClaimsDTO.getSocial());
    }
}
