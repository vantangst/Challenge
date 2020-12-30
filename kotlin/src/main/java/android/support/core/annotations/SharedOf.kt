package android.support.core.annotations

enum class SharedOf {
    /**
     * If it is fragment then using fragment as default
     */
    NONE,

    /**
     * Using activity for sharing
     */
    ACTIVITY,

    /**
     * Using parent of this fragment for sharing
     */
    PARENT
}