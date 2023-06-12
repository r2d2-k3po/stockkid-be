package net.stockkid.stockkidbe.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import net.stockkid.stockkidbe.entity.Memo;
import net.stockkid.stockkidbe.entity.QMemo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    private MemoRepository memoRepository;

    @Test
    public void testInsertDummies() {

        IntStream.rangeClosed(1,300).forEach(i -> {
            Memo memo = Memo.builder().memoText("Querydsl Sample..."+i).build();
            memoRepository.save(memo);
        });
    }

    @Test
    public void updateTest() {

        Optional<Memo> result = memoRepository.findById(300L);

        if (result.isPresent()) {
            Memo memo = result.get();
            memo.changeMemoText("Changed Text...");
            memoRepository.save(memo);
        }
    }

    @Test
    public void testQuery1() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
        QMemo qMemo = QMemo.memo;
        String keyword = "1";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expression = qMemo.memoText.contains(keyword);
        builder.and(expression);
        Page<Memo> result = memoRepository.findAll(builder, pageable);

        result.stream().forEach(memo -> {
            System.out.println(memo);
        });
    }
}
