package com.bluent.interfaces.providedInterface.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface BluentGenericRepository<T, ID>: JpaRepository<T,ID> {

    fun findAllByIsDeleted(isDeleted: Boolean = false):List<T>
    fun findAllByIsDeleted(isDeleted: Boolean = false, pageable:Pageable):Page<T>
    fun findByIdAndIsDeleted(id: ID, isDeleted: Boolean = false): T?
}