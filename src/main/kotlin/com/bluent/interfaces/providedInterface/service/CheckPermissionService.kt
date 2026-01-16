package com.bluent.interfaces.providedInterface.service

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import kotlin.collections.any
import kotlin.collections.map


interface CheckPermissionService {
    fun hasPermission(permission: String): Boolean
}
