package ua.edu.nuos.web_lab_4.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import ua.edu.nuos.web_lab_4.data.Manufacturer;

import java.util.List;

@Stateless
public class ManufacturerDao {

    @PersistenceContext
    private EntityManager em;

    public List<Manufacturer> findAll() {
        return em.createQuery("SELECT m FROM Manufacturer m ORDER BY m.name", Manufacturer.class).getResultList();
    }

    public Manufacturer findById(Integer id) {
        return em.find(Manufacturer.class, id);
    }

    public long countCars(Integer manufacturerId) {
        return em.createQuery("SELECT COUNT(c) FROM Car c WHERE c.manufacturer.id = :id", Long.class)
                .setParameter("id", manufacturerId).getSingleResult();
    }

    @Transactional
    public void create(Manufacturer m) {
        em.persist(m);
    }

    @Transactional
    public void update(Manufacturer m) {
        em.merge(m);
    }

    @Transactional
    public void deleteById(Integer id) {
        Manufacturer m = em.find(Manufacturer.class, id);
        if (m != null) em.remove(m);
    }
}
