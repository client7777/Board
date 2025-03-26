package com.example.Board.dto;

import lombok.Data;

@Data
public class MemberJoinDTO
{
    private String mid;

    private String mpw;

    private String email;

    private boolean del;

    private boolean social; //직접 회원 가입을 하는 경우 이 값은 false
}
