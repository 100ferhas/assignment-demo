package com.example.demo.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@FieldNameConstants
public class CsvEntity {

    // added this field to "fail if exists" (unique code field)
    // if we want to overwrite records instead, we can remove this field and mark code field as @Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @CsvBindByName
    @Column(unique = true)
    private String code;

    @Column
    @CsvBindByName
    private String source;

    @Column
    @CsvBindByName
    private String codeListCode;

    @Column
    @CsvBindByName
    private String displayValue;

    @Column
    @CsvBindByName
    private String longDescription;

    @Column
    @CsvBindByName
    @CsvDate(value = "dd-MM-yyyy")
    private Date fromDate;

    @Column
    @CsvBindByName
    @CsvDate(value = "dd-MM-yyyy")
    private Date toDate;
}