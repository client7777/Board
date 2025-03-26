package com.example.Board.repository.search;

import com.example.Board.domain.Board;
import com.example.Board.dto.BoardListAllDTO;
import com.example.Board.dto.BoardListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch
{
    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    //게시글 목록에서 특정한 게시글에 속한 댓글의 개수를 표시하기 위한 메서드
    Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types,
                                                      String keyword,
                                                      Pageable pageable);
    
    Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);

}