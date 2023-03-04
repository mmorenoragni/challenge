package com.example.challenge.repositories;

import com.example.challenge.entities.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OperationRepository extends CrudRepository<Operation, Long> {

    public Operation save(Operation operation);

    @Query("SELECT m FROM operation m")
    public List<Operation> findAll();

    @Query("SELECT m FROM operation m")
    public Page<Operation> findAllWithPaggination(Pageable pageable);
}
