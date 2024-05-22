package com.example.sampleproject.domain.usecase

import com.example.sampleproject.domain.network.model.Country
import com.example.sampleproject.domain.network.repository.CountryRepository
import com.example.sampleproject.data.utils.Result

/**
 * Use case responsible for fetching the list of countries.
 */
class GetCountriesUseCase(private val countryRepository: CountryRepository) {

    /**
     * Executes the use case to fetch the list of countries.
     * @return Result object containing either the list of countries on success or an error message on failure.
     */
    suspend fun execute(): Result<List<Country>> {
        return countryRepository.getCountries()
    }
}
