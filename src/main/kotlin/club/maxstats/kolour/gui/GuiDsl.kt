package club.maxstats.kolour.gui

import club.maxstats.kolour.render.drawBlur
import club.maxstats.kolour.render.drawRectangle
import club.maxstats.kolour.util.Color

data class Radius<T>(
    var topLeft: T,
    var topRight: T,
    var bottomLeft: T,
    var bottomRight: T
) {
    constructor(default: T) : this(default, default, default, default)
}
data class Sides<T>(
    var left: T,
    var top: T,
    var right: T,
    var bottom: T
) {
    constructor(default: T) : this(default, default, default, default)
}
class GuiScreen: GuiBuilder()
class GuiComponent: GuiBuilder()
sealed class GuiBuilder {
    val children = arrayListOf<GuiBuilder>()

    var color: Color = Color.none
    var width: String = "0px"
    var height: String = "0px"
    var blur: String = "0px"
    var direction: AlignDirection = AlignDirection.ROW
    var alignment: Alignment = Alignment.START
    var borderRadius: Radius<String> = Radius("0px")
    var padding: Sides<String> = Sides("0px")
    var margin: Sides<String> = Sides("0px")

    protected fun <T: GuiBuilder>init(component: T, init: T.() -> Unit): T {
        component.init()
        children.add(component)
        return component
    }
    fun component(init: GuiComponent.() -> Unit) = init(GuiComponent(), init)

    fun onRender(action: () -> Unit) {

    }
    fun onUpdate(action: () -> Unit) {

    }
    fun onClick(action: () -> Unit) {

    }
}

fun gui(init: GuiScreen.() -> Unit): GuiScreen {
    val gui = GuiScreen()
    gui.init()
    return gui
}

fun example() {
    gui {
        width = "30rem"
        height = "30rem"

        color = Color.white
        blur = "18px"
        borderRadius = Radius("10px")

        direction = AlignDirection.COLUMN
        alignment = Alignment.SPACE_BETWEEN

        component {
            width = "10rem"
            height = "10rem"

            color = Color.none
            borderRadius.topLeft = "10px"
            borderRadius.topRight = "10px"

            onClick {
                println("Clicked Component!")
            }
        }
    }
}