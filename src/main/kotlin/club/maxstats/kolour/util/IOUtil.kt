package club.maxstats.kolour.util

import club.maxstats.kolour.Kolour
import java.io.InputStream

internal fun String.asResource(): InputStream = Kolour::class.java.classLoader.getResourceAsStream(this)
    ?: throw IllegalStateException("Failed to fetch resource $this")

internal fun InputStream.readToString() = this.readBytes().toString(Charsets.UTF_8)
