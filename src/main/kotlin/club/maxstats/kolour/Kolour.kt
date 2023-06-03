package club.maxstats.kolour

import org.lwjgl.opengl.Display

/* Main class only handles weave subscriptions essential for the rendering library to work */
/* If you wish to use this library for other modding frameworks, you will have to hook these events yourself */
class Kolour {
    companion object {
        var scale: KolourScale = KolourScale(1f)
    }
}

open class KolourScale(
    val scaleFactor: Float
) {
    open fun getTrueScale() = scaleFactor
    fun getScaledWidth() = Display.getWidth().toFloat() / scaleFactor
    fun getScaledHeight() = Display.getHeight().toFloat() / scaleFactor
}

class MinecraftScale(
    scaleFactor: Float
): KolourScale(scaleFactor) {
    override fun getTrueScale() = scaleFactor / 2
}