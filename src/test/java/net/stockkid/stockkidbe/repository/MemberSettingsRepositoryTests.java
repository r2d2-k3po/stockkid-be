package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.MemberSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.IntStream;

@SpringBootTest
public class MemberSettingsRepositoryTests {

    @Autowired
    private MemberSettingsRepository memberSettingsRepository;

    @Test
    public void insertMemberSettings() {
        IntStream.rangeClosed(1,25).forEach(i -> {
            MemberSettings memberSettings = MemberSettings.builder()
                    .memberId((long) i)
                    .build();
            memberSettingsRepository.save(memberSettings);
        });
    }
}
