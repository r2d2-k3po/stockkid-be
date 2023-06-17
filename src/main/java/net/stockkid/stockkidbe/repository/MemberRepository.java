package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.username =:username and m.fromSocial =:fromSocial")
    Optional<Member> findByUsername(@Param("username") String username, @Param("fromSocial") boolean fromSocial);
}
