package com.example.Board.service;

import com.example.Board.dto.PageRequestDTO;
import com.example.Board.dto.PageResponseDTO;
import com.example.Board.dto.ReplyDTO;

public interface ReplyService
{
    Long register(ReplyDTO replyDTO); // 댓글 등록 처리

    ReplyDTO read(Long rno); // 댓글 읽기

    void modify(ReplyDTO replyDTO); // 댓글 수정

    void remove(Long rno); // 댓글 삭제

    // 특정 게시물의 댓글 목록을 페이징 처리
    PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO); 

}