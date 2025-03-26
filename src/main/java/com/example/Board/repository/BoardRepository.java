package com.example.Board.repository;

import com.example.Board.domain.Board;
import com.example.Board.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> , BoardSearch
{
    @Query(value = "select now()", nativeQuery = true)
    String getTime();

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithImages(Long bno);
}

/*
JPARepository를 상속받으면 기본적인 CRUD 메서드가 자동으로 제공
save() -> 엔티티 저장
findById() -> 기본 키로 엔티티 조회
findAll() -> 모든 엔티티 조회
deleteById() -> 기본 키로 엔티티 삭제
기타 페이징 및 정렬 지원 메서드

Board와 연결 : Board 엔티티와 데이터베이스 간의 매핑 역할을 함. 데이터베이스의 Board 테이블에 접근하는 모든 작업은 이 레포지토리를
통해 이루어짐
*/