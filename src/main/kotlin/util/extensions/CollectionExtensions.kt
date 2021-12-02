package util.extensions

import kotlin.reflect.KClass

fun <T:Any> Collection<T>.findByClass(classType: KClass<out T>): T? {
    return this.firstOrNull { item -> item::class == classType }
}