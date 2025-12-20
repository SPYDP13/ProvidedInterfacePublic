package com.bluent.interfaces.providedInterface.controller

import com.bluent.annotations.annotation.BluentCheckPermission
import com.bluent.interfaces.providedInterface.model.BluentGenericModel
import com.bluent.interfaces.providedInterface.model.MultiCreateResponse
import com.bluent.interfaces.providedInterface.model.PagingRequest
import com.bluent.interfaces.providedInterface.model.sync.DataSyncComplement
import com.bluent.interfaces.providedInterface.model.sync.DataSyncDTO
import com.bluent.interfaces.providedInterface.model.sync.OfflineSyncDTO
import com.bluent.interfaces.providedInterface.repository.BluentGenericRepository
import com.bluent.interfaces.providedInterface.service.BluentGenericService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

interface BluentGenericController<
        DTO,
        RESPONSE,
        ID,
        T:BluentGenericModel<ID, DTO, RESPONSE>,
        REPO: BluentGenericRepository<T, ID>,
        SERVICE:BluentGenericService<DTO, RESPONSE, ID, T, REPO>
        > {

    var service: SERVICE

    @BluentCheckPermission("create")
    @PostMapping("create")
    fun create(@RequestBody dto: DTO): RESPONSE = service.create(dto)

    @BluentCheckPermission("create")
    @PostMapping("createMulti")
    fun createMulti(@RequestBody dto: List<DTO>): MultiCreateResponse<RESPONSE, DTO> = service.createMulti(dto)

    @BluentCheckPermission("update")
    @PostMapping("update")
    fun update(@RequestBody dto: DTO): RESPONSE = service.update(dto)

    @BluentCheckPermission("read")
    @GetMapping("getAll")
    fun getAll(): List<DTO> = service.getAll()

    @BluentCheckPermission("read")
    @GetMapping("getById/{id}")
    fun getById(@PathVariable id: ID): RESPONSE = service.getById(id)

    @BluentCheckPermission("read")
    @PostMapping("getAllWithPaging")
    fun getAllWithPaging(@RequestBody request: PagingRequest): Page<RESPONSE> = service.getAllWithPaging(request)

    @BluentCheckPermission("delete")
    @DeleteMapping("delete/{id}")
    fun delete(@PathVariable id: ID): Boolean = service.delete(id)

    @BluentCheckPermission("delete")
    @DeleteMapping("deleteAll")
    fun deleteAll(): Boolean = service.deleteAll()

    @BluentCheckPermission("update")
    @PostMapping("syncOnline")
    fun syncOnline(@RequestBody dto: DataSyncDTO<DTO, RESPONSE>): DataSyncDTO<DTO, RESPONSE> = service.syncOnline(dto)

    @BluentCheckPermission("update")
    @PostMapping("syncOffline")
    fun syncOffline(@RequestBody dto: OfflineSyncDTO): MutableList<RESPONSE> = service.syncOffline(dto.date)

}