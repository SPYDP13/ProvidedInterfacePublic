package com.bluent.interfaces.providedInterface.model.sync

import java.time.LocalDateTime

data class DataSyncComplement<DATA, RESPONSE>(
    val data: DATA,
    val sync: Boolean?,
    val operation: DataSyncComplementOperation?,
    val operationDate: LocalDateTime?,
    val oldId: String? = null,
    val responses: RESPONSE? = null,
){
    enum class DataSyncComplementOperation{
        CREATE, UPDATE, DELETE, NULL
    }
}
