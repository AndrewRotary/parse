package com.currencyparser.repository;

import com.currencyparser.domain.CurrencyPair;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Root on 07.08.2017.
 */
public interface CurrencyPairRepository extends CrudRepository<CurrencyPair, Long> {
    @Override
    List<CurrencyPair> findAll();
}
