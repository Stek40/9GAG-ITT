package com.example.springproject.repositories;

import com.example.springproject.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country,Long> {
    Country findAllById(long id);

}
