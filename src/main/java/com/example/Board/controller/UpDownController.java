package com.example.Board.controller;

import com.example.Board.dto.upload.UploadFileDTO;
import com.example.Board.dto.upload.UploadResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController
{
    // import 시에 springframework으로 시작하는 Value
    // application.properties 파일의 설정 정보를 읽어서 변수의 값으로 사용할 수 있음
    @Value("${com.example.upload.path}")
    //uploadPath는 나중에 파일을 업로드하는 경로로 사용
    private String uploadPath;

    @Operation(description = "POST 방식으로 파일 업로드")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(@ModelAttribute UploadFileDTO uploadFileDTO) // 업로드 결과를 반환하도록 설정
    {
        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null)
        {
            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {

                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

                boolean image = false;

                try
                {
                    multipartFile.transferTo(savePath); // 실제 파일 저장

                    //이미지 파일 종류라면
                    if(Files.probeContentType(savePath).startsWith("image"))
                    {
                        image = true;

                        //섬네일을 생성
                        File thumbFile = new File(uploadPath, "s_" + uuid+"_"+originalName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                        
                        /*
                        * 썸네일 이미지는 업로드하는 파일이 이미지일 때만 처리하도록 구성
                        * 파일 이름은 맨 앞에 s_로 시작하도록 구성
                        * */
                    }
                    list.add(UploadResultDTO.builder()
                            .uuid(uuid)
                            .fileName(originalName)
                            .img(image).build()
                    );
                    
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            });
            return list;
        }
        return null;
    }

    @Operation(description = "GET방식으로 업로드된 파일 조회")
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName)
    {
        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType( resource.getFile().toPath() ));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Operation(description = "DELETE방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName)
    {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        
        boolean removed = false;

        try 
        {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            //썸네일이 존재한다며
            if(contentType.startsWith("image"))
            {
                File thumbnailFile = new File(uploadPath+File.separator +"s_" +
                        fileName);

                thumbnailFile.delete();
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);

        return resultMap;
    }
}

