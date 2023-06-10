package club.maxstats.kolour

import club.maxstats.kolour.gui.*
import club.maxstats.kolour.util.Color
import org.lwjgl.LWJGLException
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.PixelFormat
import kotlin.math.abs

private const val width = 1500
private const val height = 800

fun main() {
    /* Initialization */
    val gui: GuiBuilder
    try {
        val pixFmt = PixelFormat().withStencilBits(8)
        Display.setDisplayMode(DisplayMode(width, height))
        Display.setTitle("LWJGL Render Test")
        Display.create(pixFmt)
//        Kolour.scale = MinecraftScale(4f)
        gui = createGui()
    } catch (ex: LWJGLException) {
        ex.printStackTrace()
        return
    }

    glEnable(GL_TEXTURE_2D)
    glShadeModel(GL_SMOOTH)
    glDisable(GL_DEPTH_TEST)
    glDisable(GL_LIGHTING)
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
    glClearDepth(1.0)

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glViewport(0, 0, width, height)
    glMatrixMode(GL_MODELVIEW)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0.0, width.toDouble(), height.toDouble(), 0.0, 1.0, -1.0)
    glMatrixMode(GL_MODELVIEW)

    /* Render */
    while (!Display.isCloseRequested()) {
        Display.update()
        Display.sync(60)
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        glClearDepth(1.0)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)

        val mouseX = Mouse.getX()
        val mouseY = abs(Mouse.getY() - height)

        gui.update(mouseX, mouseY)
        gui.render(mouseX, mouseY)
    }

    Display.destroy()
}

private fun createGui(): GuiBuilder {
    return gui {
        position = Position.FIXED
        alignItems = ItemAlignment.MIDDLE
        alignContent = ContentAlignment.START
        width = 100.vw
        height = 100.vh
//        padding = Sides(0.px, 2.rem, 0.px, 0.px)
        backgroundColor = Color(0, 0, 0, 50)
        blur = 18.px

        component {
            text = "Component One"
            backgroundColor = Color(150, 0, 150, 255)
            width = 10.rem
            height = 10.rem
            borderRadius = Radius(10.px)
        }
        component {
            position = Position.RELATIVE
            text = "Component Two"
            alignItems = ItemAlignment.MIDDLE
            alignContent = ContentAlignment.MIDDLE
            backgroundColor = Color(0, 150, 150, 255)
            width = 20.rem
            height = 20.rem
//            margin.left = 10.rem
            borderRadius = Radius(10.px)

            component {
                backgroundColor = Color(0, 0, 0, 255)
                width = 5.rem
                height = 5.rem
            }
        }
    }
}
