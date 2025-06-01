package org.example.controller;

import org.example.service.Nem12ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/parser")
public class Nem12ParserController {

    private final Nem12ZipService zipService;

    @Autowired
    public Nem12ParserController(Nem12ZipService zipService) {
        this.zipService = zipService;
    }

    @PostMapping(path = "/zip", consumes = "multipart/form-data")
    public ResponseEntity<?> parseFromZip(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file uploaded"));
        }
        try {
            long totalRows = zipService.processZip(file);
            return ResponseEntity.ok(Map.of("rowsInserted", totalRows));
        }  catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Import failed: " + ex.getMessage()));
        }
    }
}
