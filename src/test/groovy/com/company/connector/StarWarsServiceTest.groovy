package com.company.connector


import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import spock.lang.Specification

import static com.company.connector.StarWarsService.*

class StarWarsServiceTest extends Specification {

    /**
     * Service integration test - internet required
     */
    def should_retrieve_luke_data_using_retrofit() {
        given: 'A service'
        def httpClient = StarWarsConnector.createHttpClient(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        def service = StarWarsConnector.createService(httpClient, "http://swapi.dev/")

        when: 'Searching for luke'
        def call = service.person("Luke")
        def Response<PersonResponse> response = call.execute()

        then: 'Should contain Luke data'
        assert response.isSuccessful() == true
        assert response.body.persons.size() == 1
        assert response.body.persons[0].name == "Luke Skywalker"
    }
}