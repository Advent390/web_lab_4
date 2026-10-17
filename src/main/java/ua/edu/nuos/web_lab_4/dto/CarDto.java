package ua.edu.nuos.web_lab_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDto implements Serializable {
    Integer id;
    String model;
    Integer productionYear;
    BigDecimal price;
    Integer mileage;
    String color;
    String fuelType;
    Integer manufacturerId;
    String manufacturerName;
}
