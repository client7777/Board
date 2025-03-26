package com.example.Board.service;

import com.example.Board.dto.MemberJoinDTO;


public interface MemberService {

    static class MidExistException extends Exception
    {
        //같은 아이디가 존재하는 경우 예외 발생
    }

    void join(MemberJoinDTO memberJoinDTO)throws MidExistException ;
}
