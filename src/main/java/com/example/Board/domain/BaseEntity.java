package com.example.Board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
// 이 클래스는 실제 데이터베이스 테이블과 매핑되지 않지만, 상속받는 클래스가 이 클래스의 필드들을 자신의 테이블에 포함되게 함
@MappedSuperclass
//엔티티가 변경될 때 특정 리스너를 실행하도록 설정
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
abstract class BaseEntity
{
    @CreatedDate // 엔티티가 생성된 시점의 시간을 자동으로 설정
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate // 엔티티가 마지막으로 수정된 시점의 시간을 자동으로 설정
    @Column(name ="moddate" )
    private LocalDateTime modDate;
}
/*
공통 속성을 정의한 추상 클래스. 여러 엔티티 클래스에서 공통적으로 사용할 수 있도록 설계
중복 코드를 줄이고 엔티티의 생성 및 수정 시간을 자동으로 관리
*/