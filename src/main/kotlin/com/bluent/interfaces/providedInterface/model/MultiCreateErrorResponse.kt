package com.bluent.interfaces.providedInterface.model

data class MultiCreateErrorResponse<DTO>(
    val data:DTO,
    val cause: String,
)
