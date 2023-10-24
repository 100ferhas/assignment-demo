package com.example.demo.repository.specifications;

import com.example.demo.entities.CsvEntity;
import com.example.demo.request.CsvRecordRequestDTO;
import org.springframework.data.jpa.domain.Specification;

public class CsvEntitySpecification {
    public static Specification<CsvEntity> fromFilterDTO(CsvRecordRequestDTO filter) {
        Specification<CsvEntity> spec = Specification.where(null);

        if (filter != null) {
            if (filter.getCode() != null && !filter.getCode().isBlank()) {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(CsvEntity.Fields.code), filter.getCode()));
            }

            // TODO add other filter fields when needed
        }

        return spec;
    }
}
