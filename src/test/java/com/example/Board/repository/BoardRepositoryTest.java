package com.example.Board.repository;

import com.example.Board.domain.Board;
import com.example.Board.domain.BoardImage;
import com.example.Board.dto.BoardListAllDTO;
import com.example.Board.dto.BoardListReplyCountDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTest
{
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert()
    {
        //Insert 기능 테스트
        //데이터베이스에 insert를 실행하는 기능은 JpaRepository의 save()를 통해서 이루어짐
        //save()는 현재 영속 컨텍스트 내에 데이터가 존재하는지 찾아보고 해당 엔티티 객체가 없을 때는 insert를, 존재할 때는 update를 자동 실행
        IntStream.rangeClosed(1,100).forEach(i ->
        {
            Board board = Board.builder()
                    .title("title..." +i)
                    .content("content..." + i)
                    .writer("user"+ (i % 10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO: " + result.getBno());
        });
    }

    @Test
    public void testSelect()
    {
        //특정한 번호의 게시물을 조회하는 기능은 findById()를 이용해서 처리
        //findById()의 리턴 타입은 Optional<T>
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        log.info(board);

    }

    @Test
    public void testUpdate()
    {
        //update 기능은 insert와 동일하게 save()를 통해서 처리됨
        //동일한 @Id값을 가지는 객체를 생성해서 처리할 수 있다.
        //일반적으로 엔티티 객체는 가능하면 최소한의 변경이나 변경이 없는 불변하게 설계하는 것이 좋다.
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        board.change("update..title 100", "update content 100");

        boardRepository.save(board);

    }

    @Test
    public void testDelete()
    {
        //delete는 @Id에 해당하는 값으로 deleteById()를 통해서 실행
        //데이터베이스 내부에 같은 @Id가 존재하는지 먼저 확인하고 delete문 실행
        Long bno = 3L;

        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging()
    {
        //페이징 기능 테스트
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable); // Spring data JPA가 제공하는 메서드로 페이징된 데이터를 조회

        log.info("total count: "+result.getTotalElements()); // 전체 데이터 개수
        log.info( "total pages:" +result.getTotalPages()); // 전체 페이지 수
        log.info("page number: "+result.getNumber()); // 현재 페이지의 번호
        log.info("page size: "+result.getSize()); // 한 페이지에 설정된 데이터 개수

        List<Board> todoList = result.getContent(); // 현재 페이지에 해당하는 Board 엔티티 데이터를 List<Board> 형태로 반환

        todoList.forEach(log::info); // 리스트의 각 요소를 순회하면서 로그에 출력
    }

    @Test
    public void testSearch1()
    {
        Pageable pageable = PageRequest.of(1,10, Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll()
    {
        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );
    }

    @Test
    public void testSearchAll2()
    {
        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );

        log.info(result.getTotalPages());

        log.info(result.getSize());

        log.info(result.getNumber());

        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(log::info);
    }

    @Test
    public void testSearchReplyCount()
    {
        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable );

        log.info(result.getTotalPages());

        log.info(result.getSize());

        log.info(result.getNumber());

        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(log::info);
    }

    @Test
    public void testInsertWithImages()
    {
        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for(int i=0; i<3; i++)
        {
            board.addImage(UUID.randomUUID().toString(), "file" + i + ",jpg");
        }

        boardRepository.save(board);
    }

    @Test
    public void testReadWithImages()
    {
        Optional<Board> result = boardRepository.findByIdWithImages(123L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("--------------------");
        for (BoardImage boardImage : board.getImageSet()) {
            log.info(boardImage);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages()
    {
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        //기존의 첨부파일은 삭제
        board.clearImages();

        for(int i=0; i<2; i++)
        {
            board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll()
    {
        Long bno = 100L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);

    }

    @Test
    public void testInsertAll()
    {
        for (int i = 1; i <= 100; i++)
        {

            Board board  = Board.builder()
                    .title("Title.."+i)
                    .content("Content.." + i)
                    .writer("writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++)
            {
                if(i % 5 == 0)
                {
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            boardRepository.save(board);
        }
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount()
    {
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);

        log.info("-----------------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(log::info);
    }

}