package club.maxstats.kolour.gui

import club.maxstats.kolour.util.Color

sealed class GuiBuilder {
    val children = arrayListOf<GuiBuilder>()
    val attributes = hashMapOf<String, String>()

    protected fun <T: GuiBuilder>init(component: T, init: T.() -> Unit): T {
        component.init()
        children.add(component)
        return component
    }
    fun onRender(action: () -> Unit) {

    }
    fun onUpdate(action: () -> Unit) {

    }
    fun onClick(action: () -> Unit) {

    }
    fun blur(radius: Float) {
        attributes["blur"] = radius.toString()
    }
    fun borderRadius(
        topLeftRadius: Float,
        topRightRadius: Float = topLeftRadius,
        bottomLeftRadius: Float = topLeftRadius,
        bottomRightRadius: Float = topLeftRadius
    ) {
        attributes["top_left_radius"] = topLeftRadius.toString()
        attributes["top_right_radius"] = topLeftRadius.toString()
        attributes["bottom_left_radius"] = topLeftRadius.toString()
        attributes["bottom_right_radius"] = topLeftRadius.toString()
    }
    fun color(color: Color) {
        attributes["color"] = color.toHex()
    }
    fun width(width: String) {
        attributes["width"] = width
    }
    fun height(height: String) {
        attributes["height"] = height
    }
    fun alignDirection(direction: AlignDirection) {
        attributes["align_direction"] = direction.toString()
    }
    fun alignItems(alignment: Alignment) {
        attributes["alignment"] = alignment.toString()
    }
    fun padding(
        paddingLeft: String,
        paddingTop: String = paddingLeft,
        paddingRight: String = paddingLeft,
        paddingBottom: String = paddingLeft
    ) {
        attributes["padding_left"] = paddingLeft
        attributes["padding_top"] = paddingTop
        attributes["padding_right"] = paddingRight
        attributes["padding_bottom"] = paddingBottom
    }
    fun margin(
        marginLeft: String,
        marginTop: String = marginLeft,
        marginRight: String = marginLeft,
        marginBottom: String = marginLeft
    ) {
        attributes["margin_left"] = marginLeft
        attributes["margin_top"] = marginTop
        attributes["margin_right"] = marginRight
        attributes["margin_bottom"] = marginBottom
    }
}
class GuiScreen: GuiBuilder()
class GuiComponent: GuiBuilder()
fun gui(init: GuiScreen.() -> Unit): GuiScreen {
    val gui = GuiScreen()
    gui.init()
    return gui
}
fun component(init: GuiComponent.() -> Unit): GuiComponent {
    val component = GuiComponent()
    component.init()
    return component
}

fun example() {
    gui {
        width("30rem")
        height("30rem")

        color(Color.white)
        blur(18f)
        borderRadius(10f)

        alignDirection(AlignDirection.COLUMN)
        alignItems(Alignment.SPACE_BETWEEN)

        component {
            color(Color(255, 0, 0, 255))
            borderRadius(
                10f,
                10f,
                0f,
                0f
            )
            width("10rem")
            height("10rem")

            onClick {
                println("Clicked Component!")
            }
        }
    }
}