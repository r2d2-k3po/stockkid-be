package net.stockkid.stockkidbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.stockkid.stockkidbe.dto.ScreenCompositionDTO;
import net.stockkid.stockkidbe.dto.ScreenSettingDTO;
import net.stockkid.stockkidbe.dto.ScreenTitlesDTO;
import net.stockkid.stockkidbe.entity.MemberSettings;
import net.stockkid.stockkidbe.repository.MemberSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemberSettingsServiceImpl implements MemberSettingsService {

    private final MemberSettingsRepository memberSettingsRepository;

    @Value("${default.settings.memberId}")
    private Long defaultSettingsMemberId;

    @Override
    public void saveScreenComposition(ScreenCompositionDTO dto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<MemberSettings> optionalMemberSettings = memberSettingsRepository.findById(memberId);
        MemberSettings existingMemberSettings = optionalMemberSettings.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        String setterNameScreenTitle = "setScreenTitle" + dto.getNumber();
        String setterNameScreenSetting = "setScreenSetting" + dto.getNumber();

        Method setterMethodScreenTitle = MemberSettings.class.getMethod(setterNameScreenTitle, String.class);
        Method setterMethodScreenSetting = MemberSettings.class.getMethod(setterNameScreenSetting, String.class);

        setterMethodScreenTitle.invoke(existingMemberSettings, dto.getScreenTitle());
        setterMethodScreenSetting.invoke(existingMemberSettings, dto.getScreenSetting());
        memberSettingsRepository.save(existingMemberSettings);
    }

    @Override
    public ScreenSettingDTO loadScreenSetting(String number) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<MemberSettings> optionalMemberSettings = memberSettingsRepository.findById(memberId);
        MemberSettings existingMemberSettings = optionalMemberSettings.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        String getterNameScreenSetting = "getScreenSetting" + number;
        Method getterMethodScreenSetting = MemberSettings.class.getMethod(getterNameScreenSetting);

        ScreenSettingDTO screenSettingDTO = new ScreenSettingDTO();
        String screenSetting = (String) getterMethodScreenSetting.invoke(existingMemberSettings);
        screenSettingDTO.setScreenSetting(screenSetting);

        return screenSettingDTO;
    }

    @Override
    public ScreenSettingDTO loadScreenSettingDefault(String number) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Optional<MemberSettings> optionalMemberSettings = memberSettingsRepository.findById(defaultSettingsMemberId);
        MemberSettings existingMemberSettings = optionalMemberSettings.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        String getterNameScreenSetting = "getScreenSetting" + number;
        Method getterMethodScreenSetting = MemberSettings.class.getMethod(getterNameScreenSetting);

        ScreenSettingDTO screenSettingDTO = new ScreenSettingDTO();
        String screenSetting = (String) getterMethodScreenSetting.invoke(existingMemberSettings);
        screenSettingDTO.setScreenSetting(screenSetting);

        return screenSettingDTO;
    }

    @Override
    public ScreenTitlesDTO loadScreenTitles() {

        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<MemberSettings> optionalMemberSettings = memberSettingsRepository.findById(memberId);
        MemberSettings existingMemberSettings = optionalMemberSettings.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        ScreenTitlesDTO screenTitlesDTO = new ScreenTitlesDTO();
        screenTitlesDTO.setScreenTitle1(existingMemberSettings.getScreenTitle1());
        screenTitlesDTO.setScreenTitle2(existingMemberSettings.getScreenTitle2());
        screenTitlesDTO.setScreenTitle3(existingMemberSettings.getScreenTitle3());

        return screenTitlesDTO;
    }

    @Override
    public ScreenTitlesDTO loadScreenTitlesDefault() {

        Optional<MemberSettings> optionalMemberSettings = memberSettingsRepository.findById(defaultSettingsMemberId);
        MemberSettings existingMemberSettings = optionalMemberSettings.orElseThrow(() -> new IllegalArgumentException("memberId not found"));

        ScreenTitlesDTO screenTitlesDTO = new ScreenTitlesDTO();
        screenTitlesDTO.setScreenTitle1(existingMemberSettings.getScreenTitle1());
        screenTitlesDTO.setScreenTitle2(existingMemberSettings.getScreenTitle2());
        screenTitlesDTO.setScreenTitle3(existingMemberSettings.getScreenTitle3());

        return screenTitlesDTO;
    }
}
