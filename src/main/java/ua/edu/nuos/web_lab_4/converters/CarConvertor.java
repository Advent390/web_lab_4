package ua.edu.nuos.web_lab_4.converters;

import jakarta.enterprise.context.ApplicationScoped;
import ua.edu.nuos.web_lab_4.data.Car;
import ua.edu.nuos.web_lab_4.data.Manufacturer;
import ua.edu.nuos.web_lab_4.dto.CarDto;

@ApplicationScoped
public class CarConvertor {

    /** Виробника підставляє DAO за manufacturerId, тут створюємо лише посилання. */
    public Car convert(CarDto d) {
        Car c = new Car();
        c.setId(d.getId());
        c.setModel(d.getModel());
        c.setProductionYear(d.getProductionYear());
        c.setPrice(d.getPrice());
        c.setMileage(d.getMileage());
        c.setColor(d.getColor());
        c.setFuelType(d.getFuelType());
        if (d.getManufacturerId() != null) {
            Manufacturer m = new Manufacturer();
            m.setId(d.getManufacturerId());
            c.setManufacturer(m);
        }
        return c;
    }

    public CarDto convert(Car c) {
        CarDto d = new CarDto();
        d.setId(c.getId());
        d.setModel(c.getModel());
        d.setProductionYear(c.getProductionYear());
        d.setPrice(c.getPrice());
        d.setMileage(c.getMileage());
        d.setColor(c.getColor());
        d.setFuelType(c.getFuelType());
        d.setManufacturerId(c.getManufacturer().getId());
        d.setManufacturerName(c.getManufacturer().getName());
        return d;
    }
}
