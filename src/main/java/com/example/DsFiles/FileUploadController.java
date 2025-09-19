package com.example.DsFiles;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final UploadedFileRepository fileRepo;

    public FileUploadController(UploadedFileRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] data = file.getBytes();
            UploadedFile savedFile = new UploadedFile(file.getOriginalFilename(), data);
            fileRepo.save(savedFile);

            return ResponseEntity.ok("File uploaded and stored in DB: " + file.getOriginalFilename());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }
}
