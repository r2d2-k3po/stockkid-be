package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberInfoRepository extends JpaRepository<MemberInfo, Long> {
}
