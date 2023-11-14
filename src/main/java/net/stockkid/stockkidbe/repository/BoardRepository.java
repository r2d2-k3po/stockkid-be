package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByBoardCategory(BoardCategory boardCategory, Pageable pageable);
}