package ua.edu.nuos.web_lab_4.converters;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.nuos.web_lab_4.data.Manufacturer;
import ua.edu.nuos.web_lab_4.dto.ManufacturerDto;

@ApplicationScoped
public class ManufacturerConvertor {

    public Manufacturer convert(ManufacturerDto d) {
        Manufacturer m = new Manufacturer();
        m.setId(d.getId());
        m.setName(d.getName());
        m.setCountry(d.getCountry());
        m.setFoundedYear(d.getFoundedYear());
        m.setDescription(d.getDescription());
        return m;
    }

    public ManufacturerDto convert(Manufacturer m) {
        ManufacturerDto d = new ManufacturerDto();
        d.setId(m.getId());
        d.setName(m.getName());
        d.setCountry(m.getCountry());
        d.setFoundedYear(m.getFoundedYear());
        d.setDescription(m.getDescription());
        return d;
    }
}
