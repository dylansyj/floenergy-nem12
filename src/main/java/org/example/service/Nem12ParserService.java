package org.example.service;

import org.example.model.MeterReading;
import org.example.repository.Nem12Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class Nem12ParserService {

    private final Nem12Repository repo;
    private final int batchSize;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Nem12ParserService(Nem12Repository repository, @Value("${nem12.batch-size}") int batchSize) {
        this.repo = repository;
        this.batchSize = batchSize;
    }

    @Transactional
    public long parseAndInsert(BufferedReader reader) throws IOException {
        long recordsCount = 0;
        String currentNmi = null;
        int intervalMinutes = 0;
        List<MeterReading> buffer = new ArrayList<>(batchSize);

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }

            if (line.startsWith("200,")) {
                String[] parts = line.split(",", -1);
                currentNmi = parts[1];
                intervalMinutes = Integer.parseInt(parts[8]);
                continue;
            }

            if (line.startsWith("300,")) {
                String[] parts = line.split(",", -1);

                // expected number of records
                int expectedCount = (24 * 60) / intervalMinutes;

                // first 2 parts are metadata
                if (parts.length < (2 + expectedCount)) {
                    throw new IllegalArgumentException("Malformed 300 record");
                }

                for (int i = 0; i < expectedCount; i++) {
                    String raw = parts[2 + i].trim();
                    if (!raw.matches("^\\d+(?:\\.\\d+)?$")) {
                        throw new IllegalArgumentException("Malformed 300 record: slot " + (i + 1) + " is not a valid value.");
                    }
                }
                LocalDate date = LocalDate.parse(parts[1], dateFmt);

                for (int i = 0; i < expectedCount; i++) {
                    String raw = parts[2 + i].trim();
                    BigDecimal consumption = new BigDecimal(raw);

                    LocalDateTime dt = date.atStartOfDay()
                            .plusMinutes((long) intervalMinutes * i);

                    MeterReading mr = new MeterReading(currentNmi, dt, consumption);
                    buffer.add(mr);
                }
            }

            if (buffer.size() >= batchSize) {
                repo.saveAll(buffer);
                repo.flush();
                recordsCount += buffer.size();
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            repo.saveAll(buffer);
            repo.flush();
            recordsCount += buffer.size();
            buffer.clear();
        }
        return recordsCount;
    }
}
