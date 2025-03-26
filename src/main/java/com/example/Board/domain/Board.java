package com.example.Board.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity // 이 클래스가 JPA 엔티티임을 나타냄
@Getter // Lombok을 사용하여 모든 필드에 대한 Getter 메서드를 자동 생성
@Builder // 빌더 패턴 적용
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자를 자동 생성
@NoArgsConstructor // 매개변수가 없는 기본 생성자를 자동 생성
@ToString(exclude = "imageSet") // 자동으로 엔티티 객체의 필드값을 문자열로 변환하여 반환, imageset은 제외
public class Board extends BaseEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 키 생성 전략 -> 데이터베이스에서 알아서 결정하는 방식
    private Long bno; // 게시판 고유 ID, 기본키

    @Column(length = 500, nullable = false) //칼럼의 길이와 null허용여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    //제목, 내용은 수정이 가능하므로 change() 메서드 추가
    public void change(String title, String content)
    {
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<BoardImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName){

        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages()
    {
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();
    }
}
/*
BaseEntity는 Board 클래스가 상속받아 재사용, Board 클래스의 regDate, modDate 필드는 상속을 받아 자동으로 추가
BaseEntity는 모든 엔터티에서 공통적으로 사용될 수 있는 생성/수정 시간 필드를 정의하고, Board는 게시판의 구체적인 데이터 구조를 정의
*/