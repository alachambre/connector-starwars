package com.company.connector

import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.bonitasoft.engine.connector.ConnectorException
import spock.lang.Specification

import static com.company.connector.StarWarsService.*

class StarWarsConnectorTest extends Specification {

    def server
    def connector

    def setup() {
        server = new MockWebServer()
        def url = server.url("/")
        def baseUrl = "http://${url.host}:${url.port}"

        def httpClient = StarWarsConnector.createHttpClient(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        def service = StarWarsConnector.createService(httpClient, baseUrl)

        connector = new StarWarsConnector()
        connector.service = service
    }

    def cleanup() {
        server.shutdown();
    }

    /**
     * Connector unit test - no internet required
     */
    def should_fetch_person() {
        given: 'A person name'
        def name = 'Luke'
        and: 'A related person JSON response'
        def body = "{\"count\":1,\"next\":null,\"previous\":null,\"results\":[{\"name\":\"$name Skywalker\",\"height\":\"172\",\"mass\":\"77\",\"hair_color\":\"blond\",\"skin_color\":\"fair\",\"eye_color\":\"blue\",\"birth_year\":\"19BBY\",\"gender\":\"male\",\"homeworld\":\"http://swapi.dev/api/planets/1/\",\"films\":[\"http://swapi.dev/api/films/1/\",\"http://swapi.dev/api/films/2/\",\"http://swapi.dev/api/films/3/\",\"http://swapi.dev/api/films/6/\"],\"species\":[],\"vehicles\":[\"http://swapi.dev/api/vehicles/14/\",\"http://swapi.dev/api/vehicles/30/\"],\"starships\":[\"http://swapi.dev/api/starships/12/\",\"http://swapi.dev/api/starships/22/\"],\"created\":\"2014-12-09T13:50:51.644000Z\",\"edited\":\"2014-12-20T21:17:56.891000Z\",\"url\":\"http://swapi.dev/api/people/1/\"}]}"
        server.enqueue(new MockResponse().setBody(body))

        when: 'Executing connector'
        connector.setInputParameters(['name': name])
        connector.executeBusinessLogic()

        then: 'Connector output should contain the person data'
        def outputParameters = connector.outputParameters
        outputParameters.size() == 1

        def person = outputParameters.get(StarWarsConnector.PERSON_OUTPUT)
        person instanceof Person
        person.name == "Luke Skywalker"
    }

    /**
     * Connector unit test - no internet required
     */
    def should_get_unknown_person() {
        given: 'An API server'
        String body = "{\"results\":[]}"
        server.enqueue(new MockResponse().setBody(body))

        when: 'Executing business logic'
        def name = 'Luke'
        connector.setInputParameters(['name': name])
        connector.executeBusinessLogic()

        then: 'Connector should throw exception'
        def e = thrown(ConnectorException)
        e.getMessage() == "$name not found"
    }

    /**
     * Connector unit test - no internet required
     */
    def should_handle_server_error() {
        given: 'An API server'
        server.enqueue(new MockResponse().setResponseCode(500))

        when: 'Executing business logic'
        def name = 'Luke'
        connector.setInputParameters(['name': name])
        connector.executeBusinessLogic()

        then: 'Connector should throw exception'
        def e = thrown(ConnectorException)
        e.getMessage() == "Server Error"
    }
}