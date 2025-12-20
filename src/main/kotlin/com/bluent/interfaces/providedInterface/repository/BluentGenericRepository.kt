package com.bluent.interfaces.providedInterface.repository

import com.bluent.interfaces.providedInterface.model.BluentGenericModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean
import java.time.LocalDateTime


@NoRepositoryBean
interface BluentGenericRepository<T, ID>: JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

    fun findAllByIsDeleted(isDeleted: Boolean = false):List<T>
    fun findAllByIsDeleted(isDeleted: Boolean = false, pageable:Pageable):Page<T>
    fun findByIdAndIsDeleted(id: ID, isDeleted: Boolean = false): T?

    fun findByCreatedAtAfterAndIsDeleted( date: LocalDateTime, isDeleted: Boolean = false): List<T>
}