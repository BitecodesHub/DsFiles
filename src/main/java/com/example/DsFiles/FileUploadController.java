package com.example.DsFiles;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final UploadedFileRepository fileRepo;
    private final Path uploadDir = Paths.get("uploads");

    public FileUploadController(UploadedFileRepository fileRepo) throws IOException {
        this.fileRepo = fileRepo;
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path destination = uploadDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            UploadedFile savedFile = new UploadedFile(
                file.getOriginalFilename(),
                destination.toAbsolutePath().toString()
            );
            fileRepo.save(savedFile);

            return ResponseEntity.ok("File uploaded: " + file.getOriginalFilename());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }
}
