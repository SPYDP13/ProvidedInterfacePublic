package com.bluent.interfaces.providedInterface.service

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import kotlin.collections.any
import kotlin.collections.map


@Service
class CheckPermissionService {
    val log = LoggerFactory.getLogger(this.javaClass)
    fun hasPermission(permission: String): Boolean{
        val auth = SecurityContextHolder.getContext().authentication
        log.info("Auth: ${auth.name} ${auth.authorities.map { it.authority }}")
        return auth?.authorities?.any {it.authority==permission} == true
    }
}
