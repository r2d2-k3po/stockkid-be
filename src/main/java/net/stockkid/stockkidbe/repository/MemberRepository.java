package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
