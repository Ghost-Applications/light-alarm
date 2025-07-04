package cash.andrew.lightalarm.misc

import java.util.*


inline fun <reified E: Enum<E>> Set<E>.toEnumSet(): EnumSet<E> = if (isEmpty()) {
    EnumSet.noneOf(E::class.java)
} else EnumSet.copyOf(this)
