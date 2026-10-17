package ua.edu.nuos.web_lab_4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManufacturerDto implements Serializable {
    Integer id;
    String name;
    String country;
    Integer foundedYear;
    String description;
}
