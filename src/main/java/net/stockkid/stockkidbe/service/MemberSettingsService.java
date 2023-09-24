package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.MemberSettingsDTO;
import net.stockkid.stockkidbe.dto.ScreenCompositionDTO;
import net.stockkid.stockkidbe.dto.ScreenSettingDTO;
import net.stockkid.stockkidbe.dto.ScreenTitlesDTO;
import net.stockkid.stockkidbe.entity.MemberSettings;

import java.lang.reflect.InvocationTargetException;

public interface MemberSettingsService {

    void saveScreenComposition(ScreenCompositionDTO dto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    ScreenSettingDTO loadScreenSetting(String number) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    ScreenSettingDTO loadScreenSettingDefault(String number) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    ScreenTitlesDTO loadScreenTitles();

    ScreenTitlesDTO loadScreenTitlesDefault();


    default MemberSettings dtoToEntity(MemberSettingsDTO dto) {
        return MemberSettings.builder()
                .screenTitle1(dto.getScreenTitle1())
                .screenTitle2(dto.getScreenTitle2())
                .screenTitle3(dto.getScreenTitle3())
                .screenSetting1(dto.getScreenSetting1())
                .screenSetting2(dto.getScreenSetting2())
                .screenSetting3(dto.getScreenSetting3())
                .build();
    }

    default MemberSettingsDTO entityToDto(MemberSettings entity) {
        return MemberSettingsDTO.builder()
                .memberId(entity.getMemberId())
                .screenTitle1(entity.getScreenTitle1())
                .screenTitle2(entity.getScreenTitle2())
                .screenTitle3(entity.getScreenTitle3())
                .screenSetting1(entity.getScreenSetting1())
                .screenSetting2(entity.getScreenSetting2())
                .screenSetting3(entity.getScreenSetting3())
                .build();
    }
}
