package android.support.core.di

import java.util.*

internal class Module(private val mContext: DependenceContext, clazz: Class<*>) {
    var beans: MutableList<RegistryBean> = ArrayList()

    init {
        try {
            loadProvides(clazz, clazz.newInstance())
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        }

    }

    private fun loadProvides(clazz: Class<*>, classInstance: Any) {
        for (method in clazz.declaredMethods) {
            if (method.isAnnotationPresent(Provide::class.java))
                beans.add(MethodBean(mContext, method, classInstance))
        }
    }
}
