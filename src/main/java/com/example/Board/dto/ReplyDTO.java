package com.example.Board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO
{
    private Long rno;

    @NotNull
    private Long bno; // 현재 댓글이 특정한 게시물의 댓글임을 식별

    @NotEmpty
    private String replyText;

    @NotEmpty
    private String replyer;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") //JSON 처리 시에 출력 형식 포맷팅
    private LocalDateTime regDate;

    @JsonIgnore // 댓글 수정 시간은 화면에 출력할 일이 없으므로 JSON으로 변환될 때 제외
    private LocalDateTime modDate;
    
}
