package com.example.demo.service.impl;

import com.example.demo.entities.CsvEntity;
import com.example.demo.exception.AppException;
import com.example.demo.repository.CsvRepository;
import com.example.demo.repository.specifications.CsvEntitySpecification;
import com.example.demo.request.CsvRecordRequestDTO;
import com.example.demo.service.CsvService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CsvServiceImpl implements CsvService {

    private final CsvRepository csvRepository;

    @Override
    public Page<CsvEntity> getData(CsvRecordRequestDTO filter, Pageable pageable) {
        log.debug("Retrieving all data with filter: {} - pagination: {}", filter, pageable);
        return csvRepository.findAll(CsvEntitySpecification.fromFilterDTO(filter), pageable);
    }

    @Override
    public void upload(InputStream inputStream) {
        log.debug("Parsing model data...");

        try (Reader reader = new InputStreamReader(inputStream)) {
            CsvToBean<CsvEntity> builder = new CsvToBeanBuilder<CsvEntity>(reader)
                    .withType(CsvEntity.class)
                    .build();

            List<CsvEntity> parsedEntities = builder.parse();

            csvRepository.saveAll(parsedEntities);

            log.info("Model data saved successfully to database.");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new AppException("Error while reading input content");
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage(), e);
            throw new AppException("Data not saved, duplicated values in 'code' field detected!");
        }
    }

    @Override
    public void deleteAll() {
        log.debug("Deleting all CsvEntities from database...");
        csvRepository.deleteAll();
        log.info("Deleted all CsvEntities from database.");
    }
}
