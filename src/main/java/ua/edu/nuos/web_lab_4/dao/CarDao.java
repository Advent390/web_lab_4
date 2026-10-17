package ua.edu.nuos.web_lab_4.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import ua.edu.nuos.web_lab_4.data.Car;
import ua.edu.nuos.web_lab_4.data.Manufacturer;
import ua.edu.nuos.web_lab_4.dto.ReportRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CarDao {

    @PersistenceContext
    private EntityManager em;

    public List<Car> findAll() {
        return em.createQuery("SELECT c FROM Car c ORDER BY c.id", Car.class).getResultList();
    }

    public Car findById(Integer id) {
        return em.find(Car.class, id);
    }

    @Transactional
    public void create(Car car) {
        car.setManufacturer(em.getReference(Manufacturer.class, car.getManufacturer().getId()));
        em.persist(car);
    }

    @Transactional
    public void update(Car car) {
        car.setManufacturer(em.getReference(Manufacturer.class, car.getManufacturer().getId()));
        em.merge(car);
    }

    @Transactional
    public void deleteById(Integer id) {
        Car c = em.find(Car.class, id);
        if (c != null) em.remove(c);
    }

    // ---------- Запити (сторінка queries.xhtml) ----------

    /** Пошук з довільною комбінацією фільтрів (Criteria API, параметризований). */
    public List<Car> search(String model, Integer manufacturerId, String fuelType,
                            Integer yearFrom, Integer yearTo,
                            BigDecimal priceFrom, BigDecimal priceTo, Integer maxMileage) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Car> q = cb.createQuery(Car.class);
        Root<Car> c = q.from(Car.class);
        List<Predicate> p = new ArrayList<>();

        if (model != null && !model.isBlank())
            p.add(cb.like(cb.lower(c.get("model")), "%" + model.trim().toLowerCase() + "%"));
        if (manufacturerId != null)
            p.add(cb.equal(c.get("manufacturer").get("id"), manufacturerId));
        if (fuelType != null && !fuelType.isBlank())
            p.add(cb.equal(c.get("fuelType"), fuelType));
        if (yearFrom != null)
            p.add(cb.greaterThanOrEqualTo(c.get("productionYear"), yearFrom));
        if (yearTo != null)
            p.add(cb.lessThanOrEqualTo(c.get("productionYear"), yearTo));
        if (priceFrom != null)
            p.add(cb.greaterThanOrEqualTo(c.get("price"), priceFrom));
        if (priceTo != null)
            p.add(cb.lessThanOrEqualTo(c.get("price"), priceTo));
        if (maxMileage != null)
            p.add(cb.lessThanOrEqualTo(c.get("mileage"), maxMileage));

        q.select(c).where(p.toArray(new Predicate[0])).orderBy(cb.asc(c.get("price")));
        return em.createQuery(q).getResultList();
    }

    /** Звіт: кількість авто та ціни по кожному виробнику (JOIN + GROUP BY). */
    public List<ReportRow> statsByManufacturer() {
        List<Object[]> rows = em.createQuery(
                        "SELECT m.name, COUNT(c), AVG(c.price), MIN(c.price), MAX(c.price) " +
                                "FROM Car c JOIN c.manufacturer m GROUP BY m.name ORDER BY COUNT(c) DESC", Object[].class)
                .getResultList();
        List<ReportRow> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(new ReportRow((String) r[0], (Long) r[1],
                    BigDecimal.valueOf(((Number) r[2]).doubleValue()).setScale(2, java.math.RoundingMode.HALF_UP),
                    (BigDecimal) r[3], (BigDecimal) r[4]));
        }
        return result;
    }

    /** Звіт: N найдорожчих авто. */
    public List<Car> mostExpensive(int limit) {
        return em.createQuery("SELECT c FROM Car c ORDER BY c.price DESC", Car.class)
                .setMaxResults(limit).getResultList();
    }

    /** Звіт: авто, дорожчі за середню ціну (підзапит). */
    public List<Car> aboveAveragePrice() {
        return em.createQuery("SELECT c FROM Car c WHERE c.price > (SELECT AVG(c2.price) FROM Car c2) " +
                "ORDER BY c.price DESC", Car.class).getResultList();
    }

    public List<String> distinctFuelTypes() {
        return em.createQuery("SELECT DISTINCT c.fuelType FROM Car c WHERE c.fuelType IS NOT NULL ORDER BY c.fuelType",
                String.class).getResultList();
    }
}
