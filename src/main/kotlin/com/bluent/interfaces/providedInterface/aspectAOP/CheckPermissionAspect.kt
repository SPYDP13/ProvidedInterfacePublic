package com.bluent.interfaces.providedInterface.aspectAOP

import com.bluent.interfaces.providedInterface.annotation.BluentCheckPermission
import com.bluent.interfaces.providedInterface.annotation.EnableBluentAutoCheckPermission
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

import com.bluent.interfaces.providedInterface.service.CheckPermissionService
import org.springframework.security.access.AccessDeniedException
import kotlin.reflect.full.findAnnotation


@Aspect
@Component
class CheckPermissionAspect(
    var service: CheckPermissionService
) {

    @Before("@annotation(checkPermission)")
    fun checkDynamicPermission(jointPoint: JoinPoint, checkPermission: BluentCheckPermission) {
        val action = checkPermission.action

        val ressourceName = (jointPoint.target::class.findAnnotation<EnableBluentAutoCheckPermission>()?.name)
        if (ressourceName != null) { //Verification si le controller est annoté pour gerer les permission
            val permission = if (checkPermission.exhaustive) {
                action
            } else {
                action.uppercase() + "_" + ressourceName.uppercase()
            }

            println(permission)
            if (!service.hasPermission(permission)) {
                println("Not Permission")
                throw AccessDeniedException("Vous n'êtes pas autorisé à effectué cette opération !")
            }
        } else {
            println("Classe non annotée pour gérer les permissions !")
        }

    }

}