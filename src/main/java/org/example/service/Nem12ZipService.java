package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class Nem12ZipService {

    private final Nem12ParserService parserService;

    @Autowired
    public Nem12ZipService(Nem12ParserService parserService) {
        this.parserService = parserService;
    }

    @Transactional
    public long processZip(MultipartFile zipFile) throws IOException {
        long totalRowsInserted = 0;

        try (InputStream inputStream = zipFile.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    zipInputStream.closeEntry();
                    continue;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));
                long inserted = parserService.parseAndInsert(reader);
                totalRowsInserted += inserted;
                zipInputStream.closeEntry();
            }
        }

        return totalRowsInserted;
    }
}
