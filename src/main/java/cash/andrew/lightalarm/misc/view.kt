package cash.andrew.lightalarm.misc

import android.view.View
import android.view.ViewTreeObserver

/**
 * Does all the hard work of adding a ViewTreeObserver
 */
fun View.addOnGlobalLayoutListener(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}
