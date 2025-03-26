package com.example.Board.service;

import com.example.Board.dto.ReplyDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class ReplyServiceTest
{
    @Autowired
    private ReplyService replyService;

    @Test
    public void testRegister()
    {
        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("ReplyDTO Text")
                .replyer("replier")
                .bno(100L)
                .build();

        log.info(replyService.register(replyDTO));
    }
}
