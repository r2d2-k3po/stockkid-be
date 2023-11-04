package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor<Board> {

    Page<Board> findByRootIdIsNull(Pageable pageable);

    Page<Board> findByRootIdIsNullAndBoardCategory(BoardCategory boardCategory, Pageable pageable);
}
