package ua.edu.nuos.web_lab_4.beans;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import ua.edu.nuos.web_lab_4.converters.CarConvertor;
import ua.edu.nuos.web_lab_4.dao.CarDao;
import ua.edu.nuos.web_lab_4.dto.CarDto;
import ua.edu.nuos.web_lab_4.dto.ReportRow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/** Сторінка запитів: пошук за фільтрами та готові звіти. */
@Named
@ViewScoped
public class QueryBean implements Serializable {

    @Inject
    private CarDao dao;

    @Inject
    private CarConvertor convertor;

    // --- поля форми пошуку ---
    @Getter @Setter private String model;
    @Getter @Setter private Integer manufacturerId;
    @Getter @Setter private String fuelType;
    @Getter @Setter private Integer yearFrom;
    @Getter @Setter private Integer yearTo;
    @Getter @Setter private BigDecimal priceFrom;
    @Getter @Setter private BigDecimal priceTo;
    @Getter @Setter private Integer maxMileage;

    // --- результати ---
    @Getter private List<CarDto> carResults;
    @Getter private List<ReportRow> statsResults;
    @Getter private String resultTitle;

    public void search() {
        if (priceFrom != null && priceTo != null && priceFrom.compareTo(priceTo) > 0
                || yearFrom != null && yearTo != null && yearFrom > yearTo) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Помилка", "Початкове значення діапазону більше кінцевого"));
            return;
        }
        show("Результат пошуку", dao.search(model, manufacturerId, fuelType, yearFrom, yearTo,
                priceFrom, priceTo, maxMileage));
    }

    public void resetFilters() {
        model = null; manufacturerId = null; fuelType = null; yearFrom = null; yearTo = null;
        priceFrom = null; priceTo = null; maxMileage = null;
        carResults = null; statsResults = null; resultTitle = null;
    }

    public void top5() {
        show("5 найдорожчих автомобілів", dao.mostExpensive(5));
    }

    public void aboveAverage() {
        show("Автомобілі дорожчі за середню ціну", dao.aboveAveragePrice());
    }

    public void stats() {
        carResults = null;
        statsResults = dao.statsByManufacturer();
        resultTitle = "Статистика по виробниках";
    }

    private void show(String title, List<ua.edu.nuos.web_lab_4.data.Car> cars) {
        statsResults = null;
        carResults = cars.stream().map(convertor::convert).toList();
        resultTitle = title + " (" + carResults.size() + ")";
    }

    public List<String> getFuelTypes() {
        return ua.edu.nuos.web_lab_4.beans.CarBean.FUEL_TYPES;
    }
}
