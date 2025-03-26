package com.example.Board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> // 제네릭 타입을 사용하여 데이터 목록의 타입을 유연하게 처리
{
    /*
    * 페이징 결과를 화면에 표시할 때 필요한 데이터를 처리하는 클래스
    */
    private int page; // 현재 페이지 번호
    
    private int size; // 한 페이지당 데이터 개수
    
    private int total; // 전체 데이터 개수

    private int start; // 현재 시작 페이지 번호

    private int end; // 현재 화면에서 끝 페이지 번호

    private boolean prev; //이전 페이지의 존재 여부

    private boolean next; //다음 페이지의 존재 여부

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total)
    {
        if(total <= 0)
        {
            //데이터가 없으면 생성자 종료
            return;
        }

        this.page = pageRequestDTO.getPage();
        
        this.size = pageRequestDTO.getSize();

        this.total = total;
        
        this.dtoList = dtoList;

        this.end =   (int)(Math.ceil(this.page / 10.0 )) *  10; // 화면에서 마지막 번호

        this.start = this.end - 9; // 화면에서의 시작 번호

        int last =  (int)(Math.ceil((total/(double)size))); // 데이터의 개수를 계산한 마지막 페이지 번호

        this.end =  end > last ? last: end; // 실제 데이터보다 끝 페이지 번호가 크면 조정

        this.prev = this.start > 1; // 시작 페이지가 1보다 크면 이전 페이지 존재

        this.next =  total > this.end * this.size; // 남은 데이터가 있으면 다음 페이지 존재

    }
}