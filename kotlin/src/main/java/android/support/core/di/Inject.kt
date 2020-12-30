package android.support.core.di


@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject(val singleton: Boolean = false)
