package com.example.Board.repository;

import com.example.Board.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, Long>
{
    //특정 게시글에 속하는 댓글을 페이징 처리하여 조회
    //@Query -> jPQL로 쿼리를 작성할 때 사용
    @Query("select r from Reply r where r.board.bno = :bno")
    Page<Reply> listOfBoard(Long bno, Pageable pageable);
    
    //게시글에 속한 댓글 일괄 삭제
    void deleteByBoard_Bno(Long bno);
}

//Page<Reply>는 페이징 처리된 Reply 객체를 반환
//Pageable pageable은 페이징 정보를 담고 있는 객체, 어떤 페이지를 조회할지, 한 페이지에 몇 개의 댓글을 표시할지 등의 정보를 전달
//Page 객체는 결과와 함께 페이징 정보를 담고 있어, 한 페이지의 데이터와 함께 총 댓글 수, 페이지 수 등을 얻을 수 있음
