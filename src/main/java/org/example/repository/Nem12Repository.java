package org.example.repository;

import org.example.model.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Nem12Repository extends JpaRepository<MeterReading, String> {
}
