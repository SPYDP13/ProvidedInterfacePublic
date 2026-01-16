package com.bluent.interfaces.providedInterface.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BluentCheckPermission(val action: String, val exhaustive: Boolean = false)
