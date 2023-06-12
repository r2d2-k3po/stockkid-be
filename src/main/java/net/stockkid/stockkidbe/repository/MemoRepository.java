package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MemoRepository extends JpaRepository<Memo, Long>, QuerydslPredicateExecutor<Memo> {
}
