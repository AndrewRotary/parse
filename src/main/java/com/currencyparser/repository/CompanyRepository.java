package com.currencyparser.repository;

import com.currencyparser.domain.Company;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Created by Root on 15.08.2017.
 */
public interface CompanyRepository extends CrudRepository<Company, Long> {

    Company findByName(String name);
}
