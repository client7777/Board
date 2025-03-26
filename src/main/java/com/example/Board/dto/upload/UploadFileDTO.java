package com.example.Board.dto.upload;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

import java.util.List;

@Data
public class UploadFileDTO
{
    private List<MultipartFile> files;
}
