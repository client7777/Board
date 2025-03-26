package com.example.Board.service;

import com.example.Board.domain.Board;
import com.example.Board.domain.Reply;
import com.example.Board.dto.PageRequestDTO;
import com.example.Board.dto.PageResponseDTO;
import com.example.Board.dto.ReplyDTO;
import com.example.Board.repository.BoardRepository;
import com.example.Board.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2

public class ReplyServiceImpl implements ReplyService
{
    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;

    private final BoardRepository boardRepository;

    @Transactional // DB 작업이 트랜잭션 단위로 실행됨
    @Override
    public Long register(ReplyDTO replyDTO)
    {
        // 댓글이 달릴 게시글 ID 가져오기, bno를 이용해 해당 게시글을 조회
        // 게시글이 없으면 예외 발생 -> 유효하지 않은 게시글이면 댓글을 저장할 수 없음
        Board board = boardRepository.findById(replyDTO.getBno())
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));

        // 댓글 엔티티 생성
        Reply reply = Reply.builder()
                .board(board)  // board와 연결
                .replyText(replyDTO.getReplyText()) // 댓글 내용 설정
                .replyer(replyDTO.getReplyer()) // 댓글 작성자 설정
                .build();

        // 댓글 저장
        Reply savedReply = replyRepository.save(reply); // 댓글을 DB에 저장

        return savedReply.getRno();  // 저장된 댓글의 rno 반환
    }

    @Override
    public ReplyDTO read(Long rno)
    {
        Optional<Reply> replyOptional = replyRepository.findById(rno);

        Reply reply = replyOptional.orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));

        // Reply -> ReplyDTO 변환
        ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class);

        // bno 값을 수동으로 설정
        if (reply.getBoard() != null) {
            replyDTO.setBno(reply.getBoard().getBno());
        } else {
            replyDTO.setBno(null);
        }

        return replyDTO;
    }

    @Override
    public void modify(ReplyDTO replyDTO)
    {
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());

        Reply reply = replyOptional.orElseThrow();

        reply.changeText(replyDTO.getReplyText()); // 댓글의 내용만 수정 가능

        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno)
    {
        Reply reply = replyRepository.findById(rno)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found with rno: " + rno));

        replyRepository.delete(reply);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO)
    {
        //특정 게시글에 달린 댓글 목록을 페이징 처리하여 반환하는 기능

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() - 1,
                //Spring Data JPA는 0부터 시작하므로 조정
                //사용자가 1페이지 요청 시 내부줙으로는 0페이지 요청이 되도록 처리
                pageRequestDTO.getSize(), // 한 페이지에 몇 개의 댓글을 가져올지 설정
                Sort.by("rno").ascending() // 댓글 번호 기준 오름차순 설정
        );

        // 해당 게시글의 댓글 목록을 조회하고, 페이지 단위로 가져옴
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        //Reply -> ReplyDTO 변환
        List<ReplyDTO> dtoList = result.getContent().stream()
                .map(reply -> {
                    ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class);
                    if (reply.getBoard() != null) {
                        replyDTO.setBno(reply.getBoard().getBno());
                    } else
                    {
                        replyDTO.setBno(null);
                    }
                    return replyDTO;
                })
                .collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll() // 빌더 패턴으로 객체 생성
                .pageRequestDTO(pageRequestDTO) // 요청한 페이지 정보 포함
                .dtoList(dtoList) // 변환된 댓글 목록 저장
                .total((int) result.getTotalElements()) // 전체 댓글 개수 설정
                .build();
    }
}