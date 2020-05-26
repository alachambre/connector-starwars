package com.company.connector.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class Person {

    String name

    String gender

    String height

    String homeworld
}