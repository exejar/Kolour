package club.maxstats.kolour.util

import club.maxstats.kolour.Main
import java.io.InputStream

fun String.asResource(): InputStream = Main::class.java.classLoader.getResourceAsStream(this)
    ?: throw IllegalStateException("Failed to fetch resource $this")

fun InputStream.readToString() = this.readBytes().toString(Charsets.UTF_8)
