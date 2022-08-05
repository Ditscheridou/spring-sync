package de.jds.shadowstore

import java.lang.reflect.InvocationTargetException

class JvmShadowStoreFactory(private val shadowStore: Class<out ShadowStore>) : ShadowStoreFactory {

    @kotlin.Throws(
        NoSuchMethodException::class,
        InvocationTargetException::class,
        InstantiationException::class,
        IllegalAccessException::class
    )
    override fun getShadowStore(id: String): ShadowStore {
        return shadowStore.getConstructor(String::class.java).newInstance(id)
    }
}
