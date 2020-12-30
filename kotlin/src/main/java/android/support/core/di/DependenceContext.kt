package android.support.core.di

import android.app.Application
import androidx.lifecycle.ViewModel
import android.content.Context
import java.util.*

class DependenceContext private constructor(application: Application) {
    private val mRegistryBeans = HashMap<Class<*>, Bean>()

    init {
        mRegistryBeans[Context::class.java] = ApplicationBean(this, application)
    }

    /**
     * Lookup bean of clazz
     *
     * @param clazz Class you want to initialize
     * @return Bean of clazz
     */
    fun lookup(clazz: Class<*>): Bean? {
        if (Context::class.java.isAssignableFrom(clazz)) return mRegistryBeans[Context::class.java]
        if (!mRegistryBeans.containsKey(clazz)) {
            when {
                clazz.isAnnotationPresent(Inject::class.java) -> registryInject(clazz)
                ViewModel::class.java.isAssignableFrom(clazz) -> registryViewModel(clazz)
                Repository::class.java.isAssignableFrom(clazz) -> registryRepository(clazz)
                else -> throw RuntimeException("Not found injection " + clazz.simpleName)
            }
        }
        return mRegistryBeans[clazz]
    }

    private fun registryRepository(clazz: Class<*>) {
        mRegistryBeans[clazz] = ClassBean(this, clazz, true)
    }

    private fun registryViewModel(clazz: Class<*>) {
        mRegistryBeans[clazz] = ClassBean(this, clazz, false)
    }

    private fun registryInject(clazz: Class<*>) {
        val inject = clazz.getAnnotation(Inject::class.java)!!
        mRegistryBeans[clazz] = ClassBean(this, clazz, inject.singleton)
    }

    private fun registryModuleClasses(vararg classes: Class<*>) {
        for (aClass in classes) {
            registryModuleClass(aClass)
        }
    }

    private fun registryModuleClass(aClass: Class<*>) {
        val module = Module(this, aClass)
        for (bean in module.beans) {
            mRegistryBeans[bean.clazz] = bean
        }
    }

    companion object {
        lateinit var sInstance: DependenceContext
        /**
         * Load modules for [Application]
         *
         * @param application my application
         * @param classes     modules classes
         */
        fun init(application: Application, vararg classes: Class<*>) {
            sInstance = DependenceContext(application)
            sInstance.registryModuleClasses(*classes)
        }
    }
}
