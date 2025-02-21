package com.spring.nuqta.base.Services;

import com.spring.nuqta.base.Repo.BaseRepo;
import jakarta.persistence.MappedSuperclass;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@MappedSuperclass
public abstract class BaseServices<T, ID extends Number> implements Serializable {

    @Autowired
    private BaseRepo<T, ID> baseRepo;

    /*
     * @return
     */
    public List<T> findAll() {
        return baseRepo.findAll();
    }

    /*
     * @param id
     * @return
     */
    public T findById(ID id) {
        return baseRepo.findById(id).orElse(null);
    }

    /*
     * @param id
     * @return
     */
    public T getOne(ID id) {
        return baseRepo.getById(id);
    }

    /*
     * @param id
     * @return
     */
    public Optional<T> getById(ID id) {
        return baseRepo.findById(id);
    }

    /*
     * @param entity
     * @return
     */
    public T insert(T entity) {
        return baseRepo.save(entity);

    }

    /*
     * @param entity
     * @return
     */
    public T persist(T entity) {
        return baseRepo.saveAndFlush(entity);

    }

    /*
     *@param entity
     *@return
     */
    public T update(T entity) {
        return baseRepo.save(entity);
    }

    /*
     * @param id
     * @return
     */
    public void deleteById(ID id) {
        baseRepo.deleteById(id);
    }

    /*
     * @param ids
     */
    public void deleteAll(List<ID> ids) {
        ids.forEach(id -> {
            baseRepo.deleteById(id);
        });
    }

}