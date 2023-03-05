package com.example.challenge.repositories;

import com.example.challenge.entities.RequestInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestInformationRepository extends CrudRepository<RequestInformation, Long>  {
    public RequestInformation save(RequestInformation requestInformation);

    @Query("SELECT ri FROM request_information ri")
    public List<RequestInformation> findAll();

    @Query("SELECT ri FROM request_information ri")
    public Page<RequestInformation> findAllWithPaggination(Pageable pageable);
}
