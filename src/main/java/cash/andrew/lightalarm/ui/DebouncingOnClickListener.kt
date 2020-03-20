package cash.andrew.lightalarm.ui

import android.view.View

private var enabled = true
private val ENABLE_AGAIN = { enabled = true }

abstract class DebouncingOnClickListener : View.OnClickListener {

    override fun onClick(v: View) {
        if (enabled) {
            enabled = false
            v.postDelayed(ENABLE_AGAIN, 100)
            doClick(v)
        }
    }

    abstract fun doClick(v: View)
}

inline fun View.debounceClickListener(crossinline listener: () -> Unit) {
    setOnClickListener(object : DebouncingOnClickListener() {
        override fun doClick(v: View) {
            listener()
        }
    })
}
