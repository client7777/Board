package com.example.Board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO
{
    private Long bno; // 게시글의 고유 ID

    private String title; // 게시글의 제목

    private String writer; // 게시글 작성자

    private LocalDateTime regDate; // 게시글의 등록 날짜

    private Long replyCount; // 해당 게시글에 달린 댓글 개수
}

/**
 * 기존 목록 화면에서는 Board 객체를 BoardDTO로 변환시켜 내용을 출력하면 충분했지만
 * 목록 화면에서 특정한 게시물에 속한 댓글의 숫자를 같이 출력해 주기 위해서 DTO클래스 구성
 * */