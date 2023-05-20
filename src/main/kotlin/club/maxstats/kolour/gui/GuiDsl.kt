package club.maxstats.kolour.gui

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
    var width: MeasurementUnit = 0.px
    var height: MeasurementUnit = 0.px
    var blur: MeasurementUnit = 0.px
    var direction: AlignDirection = AlignDirection.ROW
    var alignment: Alignment = Alignment.START
    var borderRadius: Radius<MeasurementUnit> = Radius(0.px)
    var padding: Sides<MeasurementUnit> = Sides(0.px)
    var margin: Sides<MeasurementUnit> = Sides(0.px)

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
        width = 30.rem
        height = 30.rem

        color = Color.white
        blur = 18.px
        borderRadius = Radius(10.px)

        direction = AlignDirection.COLUMN
        alignment = Alignment.SPACE_BETWEEN

        component {
            width = 10.rem
            height = 10.rem

            color = Color.none
            borderRadius.topLeft = 10.px
            borderRadius.topRight = 10.px

            onClick {
                println("Clicked Component!")
            }
        }
    }
}