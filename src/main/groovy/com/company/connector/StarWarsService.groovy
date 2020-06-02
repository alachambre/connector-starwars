package com.company.connector


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface StarWarsService {

    @Headers("Accept: application/json")
    @GET("api/people")
    def Call<PersonResponse> person(@Query("search") String name)

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Person implements Serializable {
        String name
        String gender
        String height
        String homeworld
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PersonResponse implements Serializable {
        @JsonProperty("results")
        List<Person> persons = []
    }
}
