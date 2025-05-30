package com.example.Board.service;

import com.example.Board.domain.Board;
import com.example.Board.domain.BoardImage;
import com.example.Board.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardServiceTest
{
    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister()
    {
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("HI sample title")
                .content("sample content")
                .writer("user--")
                .build();
        Long bno = boardService.register(boardDTO);

        log.info("bno " + bno);
    }

    @Test
    public void testModify()
    {
        //변경에 필요한 데이터
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(99L)
                .title("Updated....101")
                .content("Updated content 101...")
                .build();

        //첨부파일을 하나 추가
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        boardService.modify(boardDTO);

    }

    @Test
    public void testList()
    {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .size(10)
                .build();
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);
    }

    @Test
    public void testRegisterWithImages()
    {
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...sample title")
                .content("sample content")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID() + "_aaa.jpg",
                        UUID.randomUUID() + "_bbb.jpg",
                        UUID.randomUUID() + "_Ccc.jpg"
                        )
        );

        Long bno = boardService.register(boardDTO);

        log.info("bno: " + bno);
    }

    @Test
    public void testReadAll()
    {
        Long bno = 99L;

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        for(String fileName:boardDTO.getFileNames())
        {
            log.info(fileName);
        }
    }

    @Test
    public void testRemoveAll()
    {
        Long bno = 1L;

        boardService.remove(bno);
    }

    @Test
    public void testListWithAll()
    {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO =boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {

            log.info(boardListAllDTO.getBno() + ":" + boardListAllDTO.getTitle());

            if(boardListAllDTO.getBoardImages() != null)
            {
                for(BoardImageDTO boardImage : boardListAllDTO.getBoardImages())
                {
                    log.info(boardImage);
                }
            }
            log.info("-----------------------------------------------");
        });
    }
}
