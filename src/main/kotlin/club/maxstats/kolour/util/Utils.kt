package club.maxstats.kolour.util

import kotlin.math.abs

internal fun now() = System.currentTimeMillis()
internal infix fun Float.isCloseTo(other: Float) = abs(this - other) < 0.0001f