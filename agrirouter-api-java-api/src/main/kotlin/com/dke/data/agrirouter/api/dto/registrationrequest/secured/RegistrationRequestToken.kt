package com.dke.data.agrirouter.api.dto.registrationrequest.secured

import com.dke.data.agrirouter.api.dto.onboard.inner.Authentication
import com.dke.data.agrirouter.api.dto.onboard.inner.ConnectionCriteria
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class RegistrationRequestToken {

    lateinit var account: String
    lateinit var regcode: String
    lateinit var expires: String

}
