package com.example.demo.controller;

import com.example.demo.entities.CsvEntity;
import com.example.demo.exception.AppException;
import com.example.demo.request.CsvRecordRequestDTO;
import com.example.demo.service.CsvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @GetMapping
    @Operation(summary = "Retrieve data")
    public Page<CsvEntity> getData(@Nullable @ParameterObject CsvRecordRequestDTO filter,
                                   @Nullable @ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return csvService.getData(filter, pageable);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File Uploaded"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Operation(summary = "Upload data from file")
    public ResponseEntity<?> upload(@RequestPart MultipartFile file) {
        try {
            if (file.isEmpty() || (file.getOriginalFilename() != null && !file.getOriginalFilename().endsWith(".csv"))) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid uploaded file");
            }

            csvService.upload(file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new AppException("Error while reading uploaded file");
        }
    }

    @DeleteMapping
    @Operation(summary = "Deletes all data from database")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        csvService.deleteAll();
    }
}
