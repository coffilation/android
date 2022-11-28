package com.coffilation.app.util

import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View

/**
 * @author pvl-zolotov on 29.10.2022
 */
fun CharSequence.changeUrlSpanClickAction(onUrlClick: (String) -> Unit): CharSequence {
    val spannedText = SpannableString(this)
    val urlSpans = spannedText.getSpans(0, spannedText.length, URLSpan::class.java)

    urlSpans.forEach { urlSpan ->
        val url = urlSpan.url
        val start = spannedText.getSpanStart(urlSpan)
        val end = spannedText.getSpanEnd(urlSpan)

        val clickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
                onUrlClick.invoke(url)
            }
        }

        spannedText.setSpan(clickableSpan, start, end, 0)
        spannedText.removeSpan(urlSpan)
    }

    return spannedText
}
