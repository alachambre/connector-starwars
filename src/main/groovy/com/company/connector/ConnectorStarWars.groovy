package com.company.connector

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.util.logging.Slf4j
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Slf4j
class ConnectorStarWars extends AbstractConnector {

    def static final NAME_INPUT = "name"
    def static final URL_INPUT = "url"
    def static final PERSON_OUTPUT = "person"

    def final ObjectMapper mapper = new ObjectMapper()
    def StarWarsService service

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/connector-starwars.def)
     * You should:
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    @Override
    def void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(NAME_INPUT)
        checkMandatoryStringInput(URL_INPUT)
    }

    def checkMandatoryStringInput(inputName) throws ConnectorValidationException {
        def value = getInputParameter(inputName)
        if (value in String) {
            if (!value) {
                throw new ConnectorValidationException(this, "Mandatory parameter '$inputName' is missing.")
            }
        } else {
            throw new ConnectorValidationException(this, "'$inputName' parameter must be a String")
        }
    }

    /**
     * Core method:
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    @Override
    def void executeBusinessLogic() throws ConnectorException {
        def name = getInputParameter(NAME_INPUT)
        log.info "$NAME_INPUT : $name"
        def response = getService().person(name).execute()
        if (response.isSuccessful()) {
            def persons = response.body.getPersons()
            if(!persons.isEmpty()) {
                def person = persons[0]
                setOutputParameter(PERSON_OUTPUT, person)
            }
            throw new ConnectorException("$name not found")
        }
        throw new ConnectorException(response.message())
    }

    @Override
    def void connect() throws ConnectorException {
        service = createService(getInputParameter(URL))
    }

    def static StarWarsService createService(String baseUrl) {
        new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
                .create(StarWarsService.class)
    }
}