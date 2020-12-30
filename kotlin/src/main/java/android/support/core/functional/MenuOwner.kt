package android.support.core.functional

interface MenuOwner {
    fun getCurrentId(): Int

    fun setOnIdSelectedListener(onIdSelectedListener: (id:Int) -> Unit)

    fun selectId(id: Int)
}