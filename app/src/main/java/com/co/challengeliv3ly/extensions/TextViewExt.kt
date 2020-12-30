package com.co.challengeliv3ly.extensions

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView

/**
 * Searches for all URLSpans in current text replaces them with our own ClickableSpans
 * forwards clicks to provided function.
 */
fun TextView.setHighlightClickable(stringClickable: String, onClicked: (() -> Unit)? = null) {
    val wordToSpan: Spannable = SpannableStringBuilder(text)
    wordToSpan.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                onClicked?.invoke()
            }
        },
        text.indexOf(stringClickable),
        text.length,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    wordToSpan.setSpan(
        ForegroundColorSpan(Color.parseColor("#1b76d3")),
        text.indexOf(stringClickable),
        text.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = wordToSpan
    movementMethod = LinkMovementMethod.getInstance()
}