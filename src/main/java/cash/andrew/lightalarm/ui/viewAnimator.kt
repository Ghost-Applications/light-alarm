package cash.andrew.lightalarm.ui

import android.widget.ViewAnimator
import androidx.annotation.IdRes

/**
 * Helper on getter and setter of ViewAnimator that allows you to use IDs directly instead of
 * the index into the view animator.
 */
@get:IdRes
@setparam:IdRes
var ViewAnimator.displayedChildId: Int
    get() = getChildAt(displayedChild).id
    set(id) {
        if (displayedChildId == id) {
            return
        }

        (0..childCount).forEach { i ->
            if (getChildAt(i).id == id) {
                displayedChild = i
                return
            }
        }

        throw IllegalArgumentException("No view with ID=$id")
    }
