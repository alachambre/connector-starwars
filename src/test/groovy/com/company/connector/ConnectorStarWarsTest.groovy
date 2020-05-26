
package com.company.connector

import com.company.connector.model.Person
import com.company.connector.model.PersonResponse

import retrofit2.Call
import retrofit2.Response
import spock.lang.Specification

class ConnectorStarWarsTest extends Specification {

    /**
     * Integration test - requires internet
     */
    def should_retrieve_luke_data_using_retrofit() {
        given: 'A service'
        def service = ConnectorStarWars.createService("http://swapi.dev/")

        when: 'Searching for luke'
        def call = service.person("Luke")
        def Response<PersonResponse> response = call.execute()

        then: 'Should contain Luke data'
        assert response.isSuccessful() == true
        assert response.body.persons.size() == 1
        assert response.body.persons[0].name == "Luke Skywalker"
    }

    def should_use_retrofit_service_with_connector_parameters() {
        given: 'A connector'
        def connector = new ConnectorStarWars()
        def name = 'Luke'
        def personResponse = new PersonResponse()
        def Person person = new Person()
        personResponse.getPersons().add(person)
        def service = Mock(StarWarsService)
        def Call<Response> call = Mock()
        def response =  Response.success(personResponse)
        call.execute() >> response
        service.person(name) >> call
        connector.service = service
        connector.setInputParameters(['name':name])

        when: 'Executing business logic'
        connector.executeBusinessLogic()

        then : 'Service should have been called with the parameter'
        1 * service.person(name)
    }
}