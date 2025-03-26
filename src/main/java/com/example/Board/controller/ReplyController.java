package com.example.Board.controller;

import com.example.Board.dto.PageRequestDTO;
import com.example.Board.dto.PageResponseDTO;
import com.example.Board.dto.ReplyDTO;
import com.example.Board.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor // 의존성 주입을 위함
public class ReplyController
{
    private final ReplyService replyService;

    //댓글 등록
    @Operation(description = "Replies POST") // swagger에서 API 문서를 자동으로 생성할 때 사용
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE) // POST 요청을 처리, 요청 본문을 JSON 형식으로 받음
    public Map<String,Long> register(
            @Valid @RequestBody ReplyDTO replyDTO, // 요청 본문에서 ReplyDTO 객체를 읽고 유효성을 검사
            BindingResult bindingResult)throws BindException{

        log.info(replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        //replyDTO 객체를 서비스 레이어에서 처리. 댓글을 데이터베이스에 저장하고 생성된 댓글의 번호를 리턴
        Long rno = replyService.register(replyDTO);

        resultMap.put("rno",rno);

        return resultMap;
    }


    @Operation(description = "GET 방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO)
    {
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);

        return responseDTO;
    }

    @Operation(description = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno)
    {
        ReplyDTO replyDTO = replyService.read(rno);

        return replyDTO;
    }

    @Operation(description = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno)
    {
        replyService.remove(rno);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;

    }

    @Operation(description = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> replyModify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO)
    {
        replyDTO.setRno(rno);

        replyService.modify(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }
}
