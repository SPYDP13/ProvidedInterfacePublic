package com.bluent.interfaces.providedInterface.model

import java.time.LocalDateTime

interface BluentGenericModel<ID, DTO, RESPONSE> {
    var id: ID?
    var createdAt: LocalDateTime?
    var updatedAt: LocalDateTime?
    var deletedAt: LocalDateTime?
    var isDeleted: Boolean

    fun toDTO(): DTO
    fun toResponse(): RESPONSE
}