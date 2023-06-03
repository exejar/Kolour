package club.maxstats.kolour.render

import club.maxstats.kolour.util.Color
import club.maxstats.kolour.event.ResizeWindowEvent
import club.maxstats.kolour.render.shader.BlurProgram
import club.maxstats.kolour.render.shader.RectangleProgram
import club.maxstats.kolour.util.mc
import net.weavemc.loader.api.event.SubscribeEvent
import org.lwjgl.opengl.GL11

val rectProgram = RectangleProgram()
val blurProgram = BlurProgram()

fun drawRectangle(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    topLeftRadius: Float = 4f,
    topRightRadius: Float = 4f,
    bottomLeftRadius: Float = 4f,
    bottomRightRadius: Float = 4f,
    color: Color = Color.white
) {
    rectProgram.render(x, y, width, height, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, color)
}
fun drawRectangle(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    cornerRadius: Float = 4f,
    color: Color = Color.white
) {
    drawRectangle(x, y, width, height, cornerRadius, cornerRadius, cornerRadius, cornerRadius, color)
}

fun drawBlur(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    topLeftRadius: Float = 4f,
    topRightRadius: Float = 4f,
    bottomLeftRadius: Float = 4f,
    bottomRightRadius: Float = 4f,
    blurRadius: Float = 18f
) {
    blurProgram.render(x, y, width, height, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, blurRadius)
}
fun drawBlur(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    rectRadius: Float = 4f,
    blurRadius: Float = 18f
) {
    drawBlur(x, y, width, height, rectRadius, rectRadius, rectRadius, rectRadius, blurRadius)
}

fun drawQuad(
    x: Float,
    y: Float,
    x1: Float,
    y1: Float
) {
    GL11.glBegin(GL11.GL_QUADS)
    GL11.glVertex2f(x, y)
    GL11.glVertex2f(x, y1)
    GL11.glVertex2f(x1, y1)
    GL11.glVertex2f(x1, y)
    GL11.glEnd()
}