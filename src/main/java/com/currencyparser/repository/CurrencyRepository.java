package com.currencyparser.repository;

import com.currencyparser.domain.Company;
import com.currencyparser.domain.Currency;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Currency}
 */
public interface CurrencyRepository extends CrudRepository<Currency, Long> {

    Currency findByCompanyAndNameAndDate(Company company, String name, LocalDate localDate);
    Currency findByCompanyAndName(Company company, String name);

    void deleteAllByCompany(Company company);

    Optional<List<Currency>> findAllByCompany(Company company);

}
