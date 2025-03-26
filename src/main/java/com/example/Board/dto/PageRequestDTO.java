package com.example.Board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO
{
    @Builder.Default
    private int page = 1; // 요청 페이지 번호

    @Builder.Default
    private int size = 10; // 한 페이지에 표시할 데이터 개수

    private String type; // 검색의 종류 t, c, w, tc,tw, twc

    private String keyword; // 검색 키워드

    public String[] getTypes()
    {
        if(type == null || type.isEmpty())
        {
            return null;
        }
        return type.split(""); // 검색 조건을 한 글자씩 분리해 문자열 배열로 반환
    }

    // 페이징 객체 생성
    public Pageable getPageable(String...props)
    {

        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }

    
    private String link;

    //현재 페이지, 페이지 크기, 검색 조건, 검색 키워드 등을 조합해 URL 쿼리 스트링을 생성
    public String getLink()
    {

        if(link == null){
            StringBuilder builder = new StringBuilder();

            builder.append("page=" + this.page);

            builder.append("&size=" + this.size);


            if(type != null && type.length() > 0)
            {
                builder.append("&type=" + type);
            }

            if(keyword != null)
            {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
                    // 검색 키워드를 UTF-8로 인코딩하여 안전하게 사용
                }
                catch (UnsupportedEncodingException e) {
                }
            }
            link = builder.toString();
        }
        return link;
    }
}