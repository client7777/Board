package com.example.Board.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity // JPA 엔티티임을 나타냄
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno") // 테이블 이름 지정, board_bno 컬럼에 인덱스 추가
})
@Getter
@Builder
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@ToString(exclude = "board") // board 필드는 toString()에서 제외. board가 @ManyToOne 관계이므로 순환 참조가 발생할 수 있음
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno; // 댓글의 기본 키, 자동 증가 방식

    
    @ManyToOne(fetch = FetchType.LAZY) // 댓글이 게시글에 속하므로 N:1 관계, 지연 로딩 적용
    @JoinColumn(name = "board_bno", referencedColumnName = "bno") // 외래 키를 board_bno로 지정하여 Board 엔티티의 bno와 매핑
    private Board board; // Board 타입의 객체를 board라는 변수를 이용해서 참조하는데 이때, @ManyToOne을 이용해서 다대일 관계로 구성됨을 설명

    private String replyText;

    private String replyer;

    //댓글을 수정하는 경우 Reply 객체에서 replyText만을 수정할 수 있음
    //댓글 내용만 변경할 수 있도록 setter 역할의 메서드 제공
    public void changeText(String text) 
    {
        this.replyText = text;
    }
}
//댓글 입장 -> N:1 관계 (여러 댓글이 하나의 게시글에 속함)
//게시글 입장 -> 1:N 관계 (하나의 게시글이 여러 댓글을 가질 수 있음)
//지연 로딩 -> 필요한 순간까지 데이터베이스와 연결하지 않는 방식으로 동작
//지연 로딩의 반대 개념으로 즉시 로딩이 있는데 해당 엔티티를 로딩할 때 같이 로딩하는 방식 -> 성능에 영향을 줄 수 있으므로 지연 로딩을 기본값으로 사용
