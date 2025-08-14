package com.bluent.interfaces.providedInterface.model

data class MultiCreateResponse<Response, DTO>(
    val success: MutableList<Response>,
    val failed: MutableList<MultiCreateErrorResponse<DTO>>,
)
