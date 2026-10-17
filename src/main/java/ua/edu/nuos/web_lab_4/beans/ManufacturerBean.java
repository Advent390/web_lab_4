package ua.edu.nuos.web_lab_4.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ua.edu.nuos.web_lab_4.converters.ManufacturerConvertor;
import ua.edu.nuos.web_lab_4.dao.ManufacturerDao;
import ua.edu.nuos.web_lab_4.dto.ManufacturerDto;

import java.util.List;

@Named
@RequestScoped
public class ManufacturerBean {

    @Inject
    private ManufacturerDao dao;

    @Inject
    private ManufacturerConvertor convertor;

    @Getter @Setter
    private ManufacturerDto manufacturer = new ManufacturerDto();

    @Getter
    private ManufacturerDto updateManufacturer = new ManufacturerDto();

    public List<ManufacturerDto> getManufacturers() {
        return dao.findAll().stream().map(convertor::convert).toList();
    }

    private void msg(FacesMessage.Severity s, String title, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, title, text));
    }

    private boolean validate(ManufacturerDto m) {
        boolean ok = true;
        if (m.getName() == null || m.getName().isBlank()
                || m.getCountry() == null || m.getCountry().isBlank()) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Вкажіть назву та країну виробника");
            ok = false;
        }
        if (m.getFoundedYear() != null && (m.getFoundedYear() < 1800 || m.getFoundedYear() > 2100)) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Некоректний рік заснування");
            ok = false;
        }
        if (m.getDescription() != null && m.getDescription().length() > 255) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Опис занадто довгий (макс. 255 символів)");
            ok = false;
        }
        return ok;
    }

    public void create() {
        if (!validate(manufacturer)) return;
        try {
            dao.create(convertor.convert(manufacturer));
            manufacturer = new ManufacturerDto();
            msg(FacesMessage.SEVERITY_INFO, "Готово", "Виробника створено");
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("ok", true);
        } catch (Exception e) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Не вдалося створити (можливо, така назва вже є)");
        }
    }

    public void selectForUpdate(ManufacturerDto dto) {
        if (dto == null) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Виробника не знайдено");
            return;
        }
        updateManufacturer = dto;
    }

    public void update() {
        if (updateManufacturer.getId() == null || !validate(updateManufacturer)) return;
        try {
            dao.update(convertor.convert(updateManufacturer));
            msg(FacesMessage.SEVERITY_INFO, "Готово", "Виробника оновлено");
            org.primefaces.PrimeFaces.current().ajax().addCallbackParam("ok", true);
        } catch (Exception e) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Не вдалося оновити (можливо, така назва вже є)");
        }
    }

    public void delete(Integer id) {
        if (id == null) {
            msg(FacesMessage.SEVERITY_ERROR, "Помилка", "Некоректний ID");
            return;
        }
        long cars = dao.countCars(id);
        if (cars > 0) {
            msg(FacesMessage.SEVERITY_WARN, "Неможливо видалити",
                    "У виробника є автомобілі (" + cars + "). Спочатку видаліть їх.");
            return;
        }
        dao.deleteById(id);
        msg(FacesMessage.SEVERITY_INFO, "Готово", "Виробника видалено");
    }
}
