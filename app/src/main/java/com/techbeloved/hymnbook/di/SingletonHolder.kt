package com.techbeloved.hymnbook.di


/**
 * Adapted from https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
 * A Generic singleton holder that can be used to create singletons for classes that require some argument during initialization
 * To use this, use for example, (Well don't know why pre-formatted code won't show in kotlin docs)
 * <pre>
 *  class Manager private constructor(context: Context) {
 *      init {
 *          // do init with context argument
*       }
 *      companion object: SingletonHolder<Manager, Context>(::Manager)
 *  }
 *  </pre>
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}