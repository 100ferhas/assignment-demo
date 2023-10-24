package com.example.demo.service;

import com.example.demo.entities.CsvEntity;
import com.example.demo.request.CsvRecordRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.InputStream;

public interface CsvService {
    Page<CsvEntity> getData(CsvRecordRequestDTO filter, Pageable pageable);

    void upload(InputStream inputStream);

    void deleteAll();
}
