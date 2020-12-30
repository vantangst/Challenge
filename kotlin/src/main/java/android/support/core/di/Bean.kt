package android.support.core.di

import android.app.Application
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Type

abstract class Bean(protected val context: DependenceContext) {
    abstract val instance: Any
}

internal class ApplicationBean(context: DependenceContext, application: Application) : Bean(context) {
    override val instance = application
}

internal abstract class RegistryBean(
    context: DependenceContext,
    val clazz: Class<*>,
    private val isSingleton: Boolean
) : Bean(context) {
    private var mInstance: Any? = null

    override val instance: Any
        get() {
            if (isSingleton) {
                synchronized(this) {
                    if (mInstance == null) mInstance = newInstance()
                }
                return mInstance!!
            }
            return newInstance()
        }

    private fun newInstance(): Any {
        try {
            return onCreateNewInstance(lookupArguments(getParameterTypes()))
        } catch (e: Exception) {
            throw RuntimeException(clazz.name, e)
        }
    }

    private fun lookupArguments(paramTypes: Array<Type>): Array<Any?> {
        val params = arrayOfNulls<Any>(paramTypes.size)
        for (i in paramTypes.indices) {
            val type = paramTypes[i] as Class<*>
            params[i] = context.lookup(type)!!.instance
        }
        return params
    }

    protected abstract fun onCreateNewInstance(params: Array<Any?>): Any

    protected abstract fun getParameterTypes(): Array<Type>
}

internal class ClassBean(context: DependenceContext, clazz: Class<*>, singleton: Boolean) : RegistryBean(context, clazz, singleton) {

    private val mConstructor: Constructor<*>
    private val mParameterTypes: Array<Type>

    init {
        var constructors = clazz.constructors
        if (constructors.isEmpty()) constructors = clazz.declaredConstructors
        if (constructors.isEmpty())
            throw RuntimeException(clazz.simpleName + " has no constructor")
        if (constructors.size > 1)
            throw RuntimeException(clazz.simpleName + " too many constructor")
        mConstructor = constructors[0]
        mParameterTypes = mConstructor.genericParameterTypes
    }

    override fun getParameterTypes() = mParameterTypes

    override fun onCreateNewInstance(params: Array<Any?>): Any {
        mConstructor.isAccessible = true
        val value = mConstructor.newInstance(*params)
        mConstructor.isAccessible = false
        return value
    }
}

internal class MethodBean(
    context: DependenceContext,
    private val mMethod: Method,
    private val mClassInstance: Any
) : RegistryBean(context,
    mMethod.genericReturnType as Class<*>,
    mMethod.getAnnotation(Provide::class.java).singleton
) {
    private var mParameterTypes: Array<Type> = mMethod.genericParameterTypes

    override fun getParameterTypes() = mParameterTypes

    override fun onCreateNewInstance(params: Array<Any?>): Any {
        mMethod.isAccessible = true
        val value = mMethod(mClassInstance, *params)
        mMethod.isAccessible = false
        return value
    }

}