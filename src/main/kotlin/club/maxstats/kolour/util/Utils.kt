package club.maxstats.kolour.util

import net.minecraft.client.Minecraft
import kotlin.math.abs

fun now() = System.currentTimeMillis()
infix fun Float.isCloseTo(other: Float) = abs(this - other) < 0.0001f