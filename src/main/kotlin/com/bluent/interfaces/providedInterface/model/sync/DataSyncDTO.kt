package com.bluent.interfaces.providedInterface.model.sync

data class DataSyncDTO<DATA, RESPONSE>(
    val data: MutableList<DataSyncComplement<DATA, RESPONSE>>,
)
