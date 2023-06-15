package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberRepositoryTests {

    @Test
    public void memberRoleTest() {
        String hierarchy = MemberRole.getRoleHierarchy();
        System.out.println("memberRoleTest : ");
        System.out.println(hierarchy);
    }
}
