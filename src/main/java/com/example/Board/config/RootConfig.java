package com.example.Board.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 해당 클래스가 스프링의 설정 클래스임을 명시
public class RootConfig
{
    @Bean // 해당 메서드에서 반환된 객체가 스프링 빈으로 등록, 등록된 ModelMapper는 스프링 컨텍스트에서 의존성 주입을 통해 사용 가능
    public ModelMapper getMapper()
    {
        ModelMapper modelMapper = new ModelMapper(); // 객체 간 변환 (Entity -> DTO, DTO -> Entity)을 도와주는 라이브러리
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // 필드 이름 매칭 허용
                .setFieldAccessLevel(org.modelmapper.config.Configuration.
                        AccessLevel.PRIVATE) // 매핑할 때 private 필드에 직접 접근 허용
                .setMatchingStrategy(MatchingStrategies.LOOSE); // 매칭 전략을  LOOSE로 지정. 이름이 유사하면 매핑 시도
        return modelMapper;
    }
}
/*
* BoardRepository의 모든 메서드는 서비스 계층을 통해서 DTO로 변환되어 처리되도록 구성
* 엔티티 객체는 영속 컨텍스트에서 관리되므로 가능하면 많은 계층에서 사용되지 않는 것이 좋음
* 서비스 계층에서 엔티티 객체를 DTO로 변환하거나 반대의 작업을 처리하기 위해 ModelMapper 사용
* */

/*
* Entity
* 데이터베이스 테이블과 매핑된 객체로, 영속 컨텍스트에 의해 관리됩니다.
* 직접 노출하거나 여러 계층에서 사용하면 영속성 충돌 및 데이터 누수 문제가 발생할 수 있습니다.
*
* DTO
* 데이터 전송을 목적으로 설계된 객체.
* 엔티티를 DTO로 변환하여 서비스 계층, 컨트롤러 계층에서 사용하면 데이터 관리와 처리의 안정성을 높일 수 있습니다.
*
* 엔티티를 DTO로 변환하거나, 반대로 DTO를 엔티티로 변환하는 작업은 서비스 계층에서 수행.
* 이를 통해 컨트롤러와 데이터베이스 간의 결합도를 낮추고, 엔티티의 불필요한 노출을 방지.
* */