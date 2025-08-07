package com.bluent.interfaces.providedInterface.service

import com.bluent.interfaces.providedInterface.model.BluentGenericModel
import com.bluent.interfaces.providedInterface.model.PagingRequest
import com.bluent.interfaces.providedInterface.repository.BluentGenericRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import kotlin.reflect.KClass

interface BluentGenericService<
        DTO,
        RESPONSE,
        ID,
        T: BluentGenericModel<ID, DTO, RESPONSE>,
        REPO:BluentGenericRepository<T,ID>
        >
{

    var repo: REPO

    fun create(dto: DTO): RESPONSE
    fun update(dto: DTO): RESPONSE

    fun getAll(): List<DTO>{
        return repo.findAllByIsDeleted().map { it.toDTO() }
    }

    fun getAllWithPaging(request: PagingRequest?): Page<RESPONSE>{
        val pageNumber = request?.pageNumber ?: 0
        val pageSize = request?.pageSize ?: 10
        val pageable: PageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt"))
        return repo.findAllByIsDeleted(false, pageable).map { it.toResponse() }
    }

    fun getById(id: ID): RESPONSE{
        return (repo.findByIdAndIsDeleted(id)?:throw IllegalArgumentException("Not found !")).toResponse()
    }

    fun delete(id: ID): Boolean{
        val data = repo.findByIdAndIsDeleted(id)?: throw IllegalArgumentException("Not found !")
        data.isDeleted = true
        data.deletedAt = LocalDateTime.now()
        return repo.save(data).isDeleted
    }

    fun deleteAll(): Boolean{
        val data = repo.findAllByIsDeleted()
        data.forEach{
            it.isDeleted = true
            it.deletedAt = LocalDateTime.now()
        }
        repo.saveAll(data)
        return true
    }
}