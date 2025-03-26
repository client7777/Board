package com.example.Board.controller;

import com.example.Board.dto.BoardDTO;
import com.example.Board.dto.BoardListAllDTO;
import com.example.Board.dto.PageRequestDTO;
import com.example.Board.dto.PageResponseDTO;
import com.example.Board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController
{
    private final BoardService boardService;

    @Value("${com.example.upload.path}")
    private String uploadPath; // 실제 파일 삭제를 위해 경로를 주입받음

    @GetMapping("/list") // /list 경로로 들어오는 HTTP GET 요청을 처리
    public void list(PageRequestDTO pageRequestDTO, Model model)
    {
        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listWithAll(pageRequestDTO);

        log.info(responseDTO);

        // responseDTO 객체를 뷰에 전달, 뷰에서 이 데이터를 사용할 수 있도록 responseDTO라는 이름으로 모델에 속성으로 추가
        model.addAttribute("responseDTO", responseDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')") // 특정한 권한을 가진 사용자만이 접근 가능하도록 지정
    @GetMapping("/register")
    public void registerGET()
    {
        // /board/register를 호출해서 게시물 작성 페이지를 보려고 하면 @PreAuthorize에 막혀서 로그인 페이지로 이동
        // 스프링 시큐리티는 로그인이 필요해서 로그인 페이지로 리다이렉트하는 경우 어디에서부터 로그인 페이지로 이동했는지 저장하기 때문에
        // 로그인 후에는 해당 경로로 자동 이동

    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes)
    {
        //사용자가 게시글 등록 폼을 제출하면 POST 요청이 /register로 전송

        log.info("board POST register.......");
        
        if (bindingResult.hasErrors()) 
        {   //오류가 있으면, 오류 메시지를 redirectAttributes에 담아 등록 페이지로 리다이렉트
            log.info("has errors.......");
            redirectAttributes.addFlashAttribute("errors", bindingResult.
                    getAllErrors());

            return "redirect:/board/register";
        }

        log.info(boardDTO);

        //오류가 없으면 boardService.register(boardDTO)를 호출하여 게시글을 등록하고
        //게시글 번호(bno)를 리다이렉트된 목록 페이지로 전달
        Long bno = boardService.register(boardDTO);

        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 메서드에 접근
    @GetMapping({"/read", "/modify"}) // /read, /modify 경로로 요청이 오면 메서드 호출
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model)
    {
        BoardDTO boardDTO = boardService.readOne(bno); // bno에 해당하는 게시글을 가져옴

        log.info(boardDTO);

        model.addAttribute("dto", boardDTO); // boardDTO 객체를 뷰에 담아 사용할 수 있도록 전달
    }

    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping("/modify")
    public String modify( @Valid BoardDTO boardDTO,
                          BindingResult bindingResult,
                          PageRequestDTO pageRequestDTO,
                          RedirectAttributes redirectAttributes)
    {

        log.info("board modify post......." + boardDTO);

        if(bindingResult.hasErrors())
        {
            log.info("has errors.......");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );

            redirectAttributes.addAttribute("bno", boardDTO.getBno());

            return "redirect:/board/modify?"+link;
        }

        boardService.modify(boardDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDTO.getBno());

        return "redirect:/board/read";
    }


    //다른 사용자가 작성한 게시물을 삭제하려고 한다면 로그인 페이지로 리다이렉트
    @PreAuthorize("principal.username == #boardDTO.writer")
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes)
    {
        Long bno  = boardDTO.getBno();

        log.info("remove post.. " + bno);

        boardService.remove(bno);

        //게시물이 데이터베이스 상에서 삭제되었다면 첨부파일 삭제
        log.info(boardDTO.getFileNames());

        List<String> fileNames = boardDTO.getFileNames();

        if(fileNames != null && fileNames.size() > 0)
        {
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";

    }
    //게시물을 삭제할 때는 fileNames라는 이름의 파라미터로 삭제해야 하는 모든 파일들의 정보를 전달하고 BoardService에서 삭제가
    //성공적으로 이루어지면 BoardController에서는 업로드 되어 있는 파일들을 삭제
    public void removeFiles(List<String> files) 
    {
        for (String fileName : files)
        {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

            String resourceName = resource.getFilename();

            try
            {
                String contentType = Files.probeContentType(resource.getFile().
                        toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면
                if (contentType.startsWith("image"))
                {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" +
                            fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
    }
}
