package net.stockkid.stockkidbe.service;

import net.stockkid.stockkidbe.dto.ScreenCompositionDTO;
import net.stockkid.stockkidbe.dto.ScreenSettingDTO;
import net.stockkid.stockkidbe.dto.ScreenTitlesDTO;

import java.lang.reflect.InvocationTargetException;

public interface MemberSettingsService {

    void saveScreenComposition(ScreenCompositionDTO dto) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    ScreenSettingDTO loadScreenSettingById(Long id, String number) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    ScreenTitlesDTO loadScreenTitlesById(Long id);
}
