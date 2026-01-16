package com.bluent.interfaces.providedInterface.annotation


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableBluentAutoCheckPermission(val name:String)
