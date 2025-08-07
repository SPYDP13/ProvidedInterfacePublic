package com.bluent.interfaces.providedInterface.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.UUID


//@Entity
//data class Test (
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    override var id: UUID?,
//    override var createdAt: LocalDateTime?,
//    override var updatedAt: LocalDateTime?,
//    override var deletedAt: LocalDateTime?,
//    override var isDeleted: Boolean
//
//):BluentGenericModel<UUID, Test, Test>{
//    override fun toDTO(): Test {
//        return this
//    }
//
//    override fun toResponse(): Test {
//        return this
//    }
//
//}