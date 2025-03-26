package com.example.Board.controller;

import com.example.Board.dto.BoardDTO;
import com.example.Board.dto.PageRequestDTO;
import com.example.Board.dto.PageResponseDTO;
import com.example.Board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/board")
@Log4j2
@RequiredArgsConstructor
public class BoardApiController
{
    private final BoardService boardService;

    @GetMapping("/list")
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        log.info("Fetching board list...");
        return boardService.list(pageRequestDTO);
    }

    @PostMapping("/register")
    public Long register(@Valid @RequestBody BoardDTO boardDTO) {
        log.info("Registering board...");
        return boardService.register(boardDTO);
    }

    @GetMapping("/{bno}")
    public BoardDTO read(@PathVariable Long bno)
    {
        log.info("Reading board with ID: {}", bno);
        return boardService.readOne(bno);
    }

    @PutMapping("/{bno}")
    public void modify(@PathVariable Long bno, @Valid @RequestBody BoardDTO boardDTO)
    {
        log.info("Modifying board with ID: {}", bno);
        boardService.modify(boardDTO);
    }

    @DeleteMapping("/{bno}")
    public void remove(@PathVariable Long bno)
    {
        log.info("Removing board with ID: {}", bno);
        boardService.remove(bno);
    }
}