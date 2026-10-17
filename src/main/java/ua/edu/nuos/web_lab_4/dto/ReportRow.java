package ua.edu.nuos.web_lab_4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

/** Рядок звіту "статистика по виробниках". */
@Getter
@AllArgsConstructor
public class ReportRow implements Serializable {
    private final String manufacturer;
    private final long carCount;
    private final BigDecimal avgPrice;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
}
