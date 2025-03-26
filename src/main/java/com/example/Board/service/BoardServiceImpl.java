package com.example.Board.service;

import com.example.Board.domain.Board;
import com.example.Board.dto.*;
import com.example.Board.repository.BoardRepository;
import com.example.Board.repository.ReplyRepository;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 간혹 여러 번의 데이터베이스 연결이 있을 수도 있으므로 트랜잭션 처리
public class BoardServiceImpl implements BoardService
{
    // 게시판 서비스의 실제 구현체, 서비스 계층에서 사용됨, 컨트롤러는 이 서비스 계층을 호출하여 게시판 기능을 처리

    private final ModelMapper modelMapper; // BoardDTO 객체를 Board 엔티티로 변환할 때 활용

    private final BoardRepository boardRepository; // 데이터베이스에 접근하기 위한 JPA 리포지토리

    private final ReplyRepository replyRepository;
    
    //final 키워드 사용 이유 -> 의존성 주입을 통해 초기화된 후 값이 변경되지 않도록 보장

    @Override
    public Long register(BoardDTO boardDTO) // 게시글 등록 기능 구현
    {
        //BoardDTO boardDTO -> 컨트롤러에서 전달받은 게시글 정보를 담고 있는 DTO
        
        //DTO 객체를 Board 엔티티 객체로 변환
        Board board = dtoToEntity(boardDTO);
        
        //엔티티를 데이터베이스에 저장. save() 메서드는 저장된 엔티티 객체를 반환
        Long bno = boardRepository.save(board).getBno();

        return bno;
        
        /*
        * 컨트롤러 : 사용자가 게시글 등록 요청을 보냄 -> DTO 형태로 서비스 계층에 전달
        * 서비스 : register() 메서드 호출, DTO를 엔티티로 변환하고 데이터베이스에 저장, 저장된 게시글 번호를 반환
        * 컨트롤러 : 반환된 게시글 번호를 사용하여 등록 성공 여부를 확인하거나 응답으로 반환
        * */
    }

    @Override
    public BoardDTO readOne(Long bno) //특정 게시글을 조회하고, 이를 DTO로 변환하여 반환하는 역할
    {
        Optional<Board> result = boardRepository.findByIdWithImages(bno);

        Board board = result.orElseThrow();

        BoardDTO boardDTO = entityToDTO(board);

        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO)
    {
        //게시물 수정 시에 첨부파일은 새로운 파일로 대체되기 때문에 Board의 clearImage를 실행
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());

        Board board = result.orElseThrow();

        board.change(boardDTO.getTitle(), boardDTO.getContent());

        board.clearImages();

        if(boardDTO.getFileNames() != null)
        {
            for (String fileName : boardDTO.getFileNames())
            {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }

        boardRepository.save(board);

    }

    @Override
    public void remove(Long bno)
    {
        //board 테이블의 bno 컬럼이 reply 테이블의 board_bno 컬럼과 외래키 관계를 맺고 있기 때문에, 
        //board 테이블에서 특정 게시글을 삭제하려 할 때 해당 게시글에 달린 댓글(reply)이 있으면 삭제가 불가
        //해결 방안 -> 해당 게시글의 댓글을 삭제하고 게시글을 삭제해준다.
        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO)
    {
        String[] types = pageRequestDTO.getTypes(); // 검색 조건 배열

        String keyword = pageRequestDTO.getKeyword(); // 검색 키워드

        Pageable pageable = pageRequestDTO.getPageable("bno"); // 페이징, 정렬 정보 -> bno를 기준으로 정렬

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board,BoardDTO.class)).collect(Collectors.toList());
        //현재 페이지의 Board 엔티티를 리스트로 반환하고
        //Board 객체를 BoardDTO로 변환 ModelMapper를 사용해 엔티티를 DTO로 매핑, 변환된 BoardDTO 객체를 리스트로 수집
        
        return PageResponseDTO.<BoardDTO>withAll() // 빌더 패턴으로 객체 생성
                .pageRequestDTO(pageRequestDTO) // 요청된 페이징 정보
                .dtoList(dtoList) // 변환된 DTO 리스트
                .total((int)result.getTotalElements()) // 전체 데이터 개수
                .build();
    }
    
    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO)
    {
        //게시글 목록을 조회하면서 댓글 개수를 포함한 데이터를 페이징 처리하여 반환하는 역할
        //클라이언트에서 게시글 목록을 요청하면 검색 조건과 페이징 정보를 기반으로 게시글 리스트 + 댓글 개수를 가져옴
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent()) // 현재 페이지의 데이터 리스트
                .total((int)result.getTotalElements()) // 전체 데이터 개수
                .build();
        
        //게시글 목록을 검색하면서 댓글 개수를 함께 조회
        //페이징 처리하여 PageResponseDTO 형태로 반환
        //클라이언트가 원하는 페이지, 검색 조건을 적용 가능
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO)
    {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}