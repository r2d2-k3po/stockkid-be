package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.MemberSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSettingsRepository extends JpaRepository<MemberSettings, Long> {
}
