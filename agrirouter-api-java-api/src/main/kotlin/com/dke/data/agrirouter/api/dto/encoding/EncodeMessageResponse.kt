package com.dke.data.agrirouter.api.dto.encoding

import com.dke.data.agrirouter.api.dto.onboard.OnboardingResponse

/**
 * This class holds the data to be returned from an message encoding function
 *
 * @param applicationMessageID the generated application message ID
 * @param encodedMessage the encoded message
 *
 */
data class EncodeMessageResponse(
        val applicationMessageID: String,
        val encodedMessage: String,
        val onboardingResponse: OnboardingResponse
)