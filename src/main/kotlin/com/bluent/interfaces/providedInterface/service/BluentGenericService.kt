package com.bluent.interfaces.providedInterface.service

import com.bluent.interfaces.providedInterface.model.BluentGenericModel
import com.bluent.interfaces.providedInterface.model.MultiCreateErrorResponse
import com.bluent.interfaces.providedInterface.model.MultiCreateResponse
import com.bluent.interfaces.providedInterface.model.PagingRequest
import com.bluent.interfaces.providedInterface.model.sync.DataSyncComplement
import com.bluent.interfaces.providedInterface.model.sync.DataSyncDTO
import com.bluent.interfaces.providedInterface.repository.BluentGenericRepository
import com.bluent.interfaces.providedInterface.repository.BluentGenericSpec
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import java.util.UUID
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

    fun createMulti(data: List<DTO>): MultiCreateResponse<RESPONSE, DTO> {
        val multiCreation = MultiCreateResponse<RESPONSE,DTO>(mutableListOf(), mutableListOf())
        data.forEach {
            try {
                val response = create(it)
                multiCreation.success.add(response)
            } catch (e: Exception) {
                multiCreation.failed.add(
                    MultiCreateErrorResponse(
                        data = it,
                        cause = e.message ?: "Unknown error"
                    )
                )
            }
        }
        return multiCreation
    }
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

    fun syncOnline(dto: DataSyncDTO<DTO, RESPONSE>): DataSyncDTO<DTO, RESPONSE>{
        println("Syncing>>>>>>>>>>>>> $dto")
        val finalRespone = DataSyncDTO<DTO, RESPONSE>(mutableListOf())
        dto.data.forEach {
            if (it.operation=== DataSyncComplement.DataSyncComplementOperation.CREATE){ //Operation de creation
                val toSync = it.data
//                toSync.createdAt = it.operationDate
                val created = create(toSync)
                finalRespone.data.add(

                    DataSyncComplement(
                        data = toSync,
                        operation = it.operation,
                        responses = created,
                        operationDate = null,
                        oldId = it.oldId,
                        sync = false
                    )
                )
            }
            if (it.operation=== DataSyncComplement.DataSyncComplementOperation.UPDATE){ //Operation de creation
                val toSync = it.data
//                toSync.createdAt = it.operationDate
//                toSync.id = UUID.fromString(it.oldId)
                val updated = update(toSync)
                finalRespone.data.add(
                    DataSyncComplement(
                        data = toSync,
                        operation = it.operation,
                        responses = updated,
                        operationDate = null,
                        oldId = it.oldId,
                        sync = false
                    )
                )
            }
            if (it.operation=== DataSyncComplement.DataSyncComplementOperation.DELETE){ //Operation de creation
                val toSync = it.data
//                toSync.createdAt = it.operationDate
                val deleted = delete(convertStringToUUID(it.oldId)!!)
                finalRespone.data.add(
                    DataSyncComplement(
                        data = toSync,
                        operation = it.operation,
                        responses = null,
                        operationDate = null,
                        oldId = it.oldId,
                        sync = false
                    )
                )
            }
        }
        return finalRespone
    }

    fun syncOffline(date: LocalDateTime?): MutableList<RESPONSE>{
        val spec= BluentGenericSpec<ID, DTO, RESPONSE, T>()
        val afterDatePredicate = spec.getDataAfterCreated(date)
        return repo.findAll(afterDatePredicate).map { it.toResponse() }.toMutableList()
    }

    fun convertStringToUUID(string: String?): ID? {
        return null
    }
}