package android.support.core.annotations


@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class ShareViewModel(
    val value: SharedOf = SharedOf.ACTIVITY
)