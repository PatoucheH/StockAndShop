package be.stockandshopbackend.bll.services.base;

import be.stockandshopbackend.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class BaseCRUDService<T, ID, R extends JpaRepository<T, ID>> {

    protected final R repository;

    protected BaseCRUDService(R repository) {
        this.repository = repository;
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T findById(ID id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("No record found with id : " + id)
        );
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public void deleteById(ID id) {
        if(!repository.existsById(id)) {
            throw new NotFoundException("No record found with id : " + id);
        }
        repository.deleteById(id);
    }
}
