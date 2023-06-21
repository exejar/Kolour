package club.maxstats.kolour

import club.maxstats.kolour.gui.*
import club.maxstats.kolour.render.FontStyle
import club.maxstats.kolour.util.Color
import net.minecraft.client.Minecraft
import org.lwjgl.LWJGLException
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.PixelFormat
import java.net.URLClassLoader
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
    val configCategory =
        component {
            style {
                text = "test"
                position = Position.RELATIVE
                alignItems = ItemAlignment.MIDDLE
                alignContent = ContentAlignment.MIDDLE

                width = 5.rem
                height = 5.rem
//                margin = Sides(1.rem, 0.px, 1.rem, 0.px)

                backgroundColor = Color(60, 60, 60, 255)
                borderRadius = Radius(10.px)
            }

            component {
                id = "child-in-reusable"

                style {
                    width = 3.rem
                    height = 3.rem

                    text = "Shit"

                    backgroundColor = Color(0, 0, 255, 255)
                }
            }
        }

    return gui {
        id = "main-screen"

        style {
            position = Position.FIXED
            alignItems = ItemAlignment.MIDDLE
            alignContent = ContentAlignment.MIDDLE

            width = 100.vw
            height = 100.vh

            backgroundColor = Color(25, 25, 25, 100)
            blur = 18.px
        }

        component {
            id = "content"

            style {
                position = Position.ABSOLUTE
                top = 100.vh
                width = 100.vw
                height = 1.rem
            }

            component {
                id = "left-panel"

                style {
                    backgroundColor = Color(50, 0, 0, 255)
                    position = Position.RELATIVE
                    direction = AlignDirection.COLUMN
                    alignItems = ItemAlignment.MIDDLE
                    alignContent = ContentAlignment.MIDDLE

                    width = 60.vw
                    height = 100.vh
                }

                component {
                    id = "stat-container"

                    style {
                        position = Position.RELATIVE
                        alignItems = ItemAlignment.MIDDLE
                        alignContent = ContentAlignment.MIDDLE

                        width = 60.vw
                        height = 50.vh
                    }


                    component {
                        id = "player-container"

                        style {
                            alignItems = ItemAlignment.MIDDLE
                            alignContent = ContentAlignment.MIDDLE
                            width = 5.rem
                            height = 8.rem
                            backgroundColor = Color(45, 45, 45, 255)
                            borderRadius = Radius(6.px)
                        }

//                        onRender {
//                            // render player model using minecraft's method
//                            val scale = 40
//                            renderPlayerModel(
//                                Minecraft.getMinecraft().thePlayer,
//                                (width / 2),
//                                (height / 2) + (scale - 5),
//                                0f,
//                                0f,
//                                scale
//                            )
//                        }
                    }
                }
            }
            component {
                id = "right-panel"

                style {
                    backgroundColor = Color(50, 50, 0, 255)
                    position = Position.RELATIVE
                    direction = AlignDirection.COLUMN
                    alignItems = ItemAlignment.MIDDLE
                    alignContent = ContentAlignment.MIDDLE
                    height = 100.vh
                    width = 40.vw
                }


                header {
                    style {
                        fontStyle = FontStyle.BOLD
                        text = "Seraph Settings"
                        margin.bottom = 4.rem
                    }
                }
                +configCategory
                +configCategory
                +configCategory
            }
        }
    }
}
