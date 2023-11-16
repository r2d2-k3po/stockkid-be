package net.stockkid.stockkidbe.repository;

import net.stockkid.stockkidbe.entity.Board;
import net.stockkid.stockkidbe.entity.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByBoardCategory(BoardCategory boardCategory, Pageable pageable);

    @Query("select b from Board b where b.tag1 = :tag or b.tag2 = :tag or b.tag3 = :tag")
    Page<Board> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("select b from Board b where b.boardCategory = :boardCategory and (b.tag1 = :tag or b.tag2 = :tag or b.tag3 = :tag)")
    Page<Board> findByBoardCategoryAndTag(@Param("boardCategory") BoardCategory boardCategory, @Param("tag") String tag, Pageable pageable);
}