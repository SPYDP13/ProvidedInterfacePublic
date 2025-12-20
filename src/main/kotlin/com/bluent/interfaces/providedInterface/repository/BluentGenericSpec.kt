package com.bluent.interfaces.providedInterface.repository

import com.bluent.interfaces.providedInterface.model.BluentGenericModel
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

class BluentGenericSpec<ID, DTO, RESPONSE, DATA : BluentGenericModel<ID, DTO, RESPONSE>> {
    fun getDataAfterCreated(date: LocalDateTime?): Specification<DATA?> {
        return Specification { root, query, builder ->
            var predicate = builder.conjunction()

            date?.let {
                predicate = builder.and(
                    predicate,
                    builder.greaterThan(root.get("createdAt"), date)
                )
            }


            predicate = builder.and(
                predicate,
                builder.isFalse(root.get("isDeleted"))
            )

            predicate
        }
    }
}