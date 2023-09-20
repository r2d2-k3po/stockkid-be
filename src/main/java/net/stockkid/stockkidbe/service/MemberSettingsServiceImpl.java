package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.repository.MemberSettingsRepository;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemberSettingsServiceImpl  implements MemberSettingsService{

    private final MemberSettingsRepository memberSettingsRepository;

}
