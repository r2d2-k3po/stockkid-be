package net.stockkid.stockkidbe.security;

import net.stockkid.stockkidbe.dto.JWTClaimsDTO;
import net.stockkid.stockkidbe.security.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTTests {

    @Autowired
    private JWTUtil jwtUtil;

    //get a key and save it in application.properties
    @Test
    public void generateBase64EncodedSecretKey() throws Exception {

        String base64EncodedSecretKey = JWTUtil.getBase64EncodedSecretKey();

        System.out.println("base64EncodedSecretKey : " + base64EncodedSecretKey);
    }

    @Test
    public void testEncode() throws Exception {
        String username = "jwtTestUser";
        String role = "USER";

        String token = jwtUtil.generateToken(username, role);

        System.out.println("token : " + token);
    }

    @Test
    public void testVerify() throws Exception {

        String username = "jwtTestUser";
        String role = "USER";
        String token = jwtUtil.generateToken(username, role);

        Thread.sleep(5000);

        JWTClaimsDTO jwtClaimsDTO = jwtUtil.verifyAndExtract(token);

        System.out.println("username : " + jwtClaimsDTO.getUsername());
        System.out.println("role : " + jwtClaimsDTO.getRole());
    }
}
