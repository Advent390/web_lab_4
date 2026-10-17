package ua.edu.nuos.web_lab_4.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ua.edu.nuos.web_lab_4.converters.CarConvertor;
import ua.edu.nuos.web_lab_4.dao.CarDao;
import ua.edu.nuos.web_lab_4.dto.CarDto;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Named
@RequestScoped
public class CarBean {

    public static final List<String> FUEL_TYPES = List.of("Бензин", "Дизель", "Гібрид", "Електро", "Газ/бензин");

    @Inject
    private CarDao dao;

    @Inject
    private CarConvertor convertor;

    @Getter @Setter
    private CarDto car = new CarDto();

    @Getter
    private CarDto updateCar = new CarDto();

    public List<CarDto> getCars() {
        return dao.findAll().stream().map(convertor::convert).toList();
    }

    public List<String> getFuelTypes() {
        return FUEL_TYPES;
    }

    private void msg(FacesMessage.Severity s, String title, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, title, text));
    }

    private boolean validate(CarDto c) {
        boolean ok = true;
        if (c.getModel() == null || c.getModel().isBlank()) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Вкажіть модель");
            ok = false;
        }
        if (c.getManufacturerId() == null) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Оберіть виробника");
            ok = false;
        }
        if (c.getProductionYear() == null || c.getProductionYear() < 1886
                || c.getProductionYear() > Year.now().getValue() + 1) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Некоректний рік випуску");
            ok = false;
        }
        if (c.getPrice() == null || c.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Ціна має бути більшою за 0");
            ok = false;
        }
        if (c.getMileage() != null && c.getMileage() < 0) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Пробіг не може бути від'ємним");
            ok = false;
        }
        return ok;
    }

    public void create() {
        if (!validate(car)) return;
        dao.create(convertor.convert(car));
        car = new CarDto();
        msg(FacesMessage.SEVERITY_INFO, "Готово", "Автомобіль додано");
        org.primefaces.PrimeFaces.current().ajax().addCallbackParam("ok", true);
    }

    public void selectForUpdate(Integer id) {
        var found = id == null ? null : dao.findById(id);
        if (found == null) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Автомобіль не знайдено");
            return;
        }
        updateCar = convertor.convert(found);
    }

    public void update() {
        if (updateCar.getId() == null || !validate(updateCar)) return;
        dao.update(convertor.convert(updateCar));
        msg(FacesMessage.SEVERITY_INFO, "Готово", "Автомобіль оновлено");
        org.primefaces.PrimeFaces.current().ajax().addCallbackParam("ok", true);
    }

    public void delete(Integer id) {
        if (id == null) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Некоректний ID");
            return;
        }
        dao.deleteById(id);
        msg(FacesMessage.SEVERITY_INFO, "Готово", "Автомобіль видалено");
    }
}
