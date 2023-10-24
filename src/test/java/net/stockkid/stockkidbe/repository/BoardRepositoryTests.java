package net.stockkid.stockkidbe.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

//    @Test
//    public void testInsertDummies() {
//
//        IntStream.rangeClosed(1,300).forEach(i -> {
//            Memo memo = Memo.builder().memoText("Querydsl Sample..."+i).build();
//            boardRepository.save(memo);
//        });
//    }
//
//    @Test
//    public void updateTest() {
//
//        Optional<Memo> result = memoRepository.findById(300L);
//
//        if (result.isPresent()) {
//            Memo memo = result.get();
//            memo.changeMemoText("Changed Text...");
//            memoRepository.save(memo);
//        }
//    }
//
//    @Test
//    public void testQuery1() {
//
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
//        QMemo qMemo = QMemo.memo;
//        String keyword = "1";
//        BooleanBuilder builder = new BooleanBuilder();
//        BooleanExpression expression = qMemo.memoText.contains(keyword);
//        builder.and(expression);
//        Page<Memo> result = memoRepository.findAll(builder, pageable);
//
//        result.stream().forEach(memo -> {
//            System.out.println(memo);
//        });
//    }
}
