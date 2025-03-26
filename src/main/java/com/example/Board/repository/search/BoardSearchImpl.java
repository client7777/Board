package com.example.Board.repository.search;

import com.example.Board.domain.Board;
import com.example.Board.domain.QBoard;
import com.example.Board.domain.QReply;
import com.example.Board.dto.BoardImageDTO;
import com.example.Board.dto.BoardListAllDTO;
import com.example.Board.dto.BoardListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch
{
    //QuerydslRepositorySupport -> QueryDSL을 사용할 때 유용한 메서드를 제공

    public BoardSearchImpl()
    {
        //QuerydslRepositorySupport에 Board 엔티티 클래스 전달
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) // 동적으로 쿼리를 생성하고 페이징 처리된 데이터를 처리하기 위한 메서드
    {

        QBoard board = QBoard.board; // Q도메인 객체, QueryDSL이 자동으로 생성한 클래스, Board 엔티티와 동일한 필드 및 메서드를 제공

        JPQLQuery<Board> query = from(board); // select ... from board, JPQL 쿼리 생성

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // 조건을 동적으로 생성할 때 사용하는 클래스

        booleanBuilder.or(board.title.contains("11")); // title like

        booleanBuilder.or(board.content.contains("11")); // content like

        query.where(booleanBuilder);
        
        query.where(board.bno.gt(0L));

        
        this.getQuerydsl().applyPagination(pageable, query); // 페이징 적용

        List<Board> list = query.fetch(); // 조건이 적영된 데이터를 조회하여 리스트 형태로 반환

        long count = query.fetchCount();
        
        return null;

    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) 
    {
        // types -> 검색 조건을 의미
        // t -> 제목 c -> 내용 w -> 작성자

        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        if( (types != null && types.length > 0) && keyword != null ) // 검색 조건과 키워드가 있다면
        {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type: types)
            {
                switch (type)
                {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        query.where(board.bno.gt(0L)); // bno > 0

        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch(); // 쿼리를 실행하여 조회된 데이터를 리스트 형태로 반환

        long count = query.fetchCount(); // 조회된 데이터의 총 개수를 반환

        return new PageImpl<>(list, pageable, count); // 페이징된 데이터를 반환
        /*
        * 페이징 처리의 최종 결과는 Page<T>타입을 반환하는 것이므로 Querydsl에서는 이를 직접 처리해야하는 불편함이 존재
        * Spring Data JPA에서는 이를 처리하기 위해 PageImpl이라는 클래스를 제공해서 3개의 파라미터로 생성 가능
        * List<T> : 실제 데이터 목록 Pageable : 페이지 관련 정보를 가진 객체, long : 전체 개수
        * */

    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable)
    {   
        //Querydsl을 통해 생성된 Q타입 클래스
        //각각 게시글과 댓글을 조회할 때 사용
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        //기본 쿼리 설정
        JPQLQuery<Board> query = from(board); // 테이블을 기준으로 조회
        // 댓글 테이블과 왼쪽 조인하여 게시글에 달린 댓글도 함께 조회, 댓글이 없는 게시글도 조회
        // 댓글이 특정 게시글에 속하는지 확인하는 조건
        query.leftJoin(reply).on(reply.board.eq(board)); 
        //그룹화를 통해 여러 행 을 하나의 행으로 합치는 기능, 각 게시글에 대한 댓글 수를 계산
        query.groupBy(board.bno, board.title, board.writer, board.regDate);

        //동적 검색 조건 추가
        if( (types != null && types.length > 0) && keyword != null )
        {

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // 동적 검색 조건 추가

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);
        }

        query.where(board.bno.gt(0L)); // 게시글 번호가 0보다 큰 데이터만 조회

        //Projections.bean() -> JPQL의 결과를 바로 DTO로 처리하는 기능
        //이를 위해서는 JPQLQuery 객체의 select()를 이용
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount") //DTO 필드로 변환
        ));

        this.getQuerydsl().applyPagination(pageable,dtoQuery); // 페이징 적용

        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch(); // 쿼리 실행

        long count = dtoQuery.fetch().size(); // 전체 게시글 수

        return new PageImpl<>(dtoList, pageable, count); //페이징 결과 반환
    }

    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable)
    {
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));//left join

        if((types != null && types.length > 0) && keyword != null)
        {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for(String type:types)
            {
                switch (type)
                {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            boardJPQLQuery.where(booleanBuilder);
        }
        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable, boardJPQLQuery);

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Board board1 = (Board) tuple.get(board);

            long replyCount = tuple.get(1,Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //BoardImage를 BoardImageDTO로 처리할 부분
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS);

            return dto;

        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}