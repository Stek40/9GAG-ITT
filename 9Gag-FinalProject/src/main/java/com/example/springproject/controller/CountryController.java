package com.example.springproject.controller;

import com.example.springproject.model.Country;
import com.example.springproject.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class CountryController {
    @Autowired
    CountryRepository countryRepository;

    @PostMapping("/countries")
    public void addAllCountries(@RequestBody ArrayList<Country> countries) {
        countryRepository.saveAll(countries);
    }
}
