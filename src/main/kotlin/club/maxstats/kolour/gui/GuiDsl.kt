package club.maxstats.kolour.gui

import club.maxstats.kolour.render.*
import club.maxstats.kolour.util.Color
import club.maxstats.kolour.util.mc

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
open class GuiComponent: GuiBuilder()
sealed class GuiBuilder {
    var position: Position = Position.STATIC
    var x: MeasurementUnit = 0.px
    var y: MeasurementUnit = 0.px
    var width: MeasurementUnit = 0.px
    var height: MeasurementUnit = 0.px
    var padding: Sides<MeasurementUnit> = Sides(0.px)
    var margin: Sides<MeasurementUnit> = Sides(0.px)

    var color: Color = Color.white
    var fontSize: Int = 16
    var text: String = ""
    var fontStyle: FontStyle = FontStyle.PLAIN
    var lineSpacing: MeasurementUnit = 0.px
    var wrapText: Boolean = true

    var direction: AlignDirection = AlignDirection.ROW
    var alignment: Alignment = Alignment.START

    var blur: MeasurementUnit = 0.px
    var borderRadius: Radius<MeasurementUnit> = Radius(0.px)
    var backgroundColor: Color = Color.none

    protected var fontRenderer: FontRenderer = fontManager.getFontRenderer(fontSize)
    protected val children = arrayListOf<GuiBuilder>()

    protected fun <T: GuiBuilder>init(component: T, init: T.() -> Unit): T {
        component.init()
        children.add(component)
        return component
    }
    fun component(init: GuiComponent.() -> Unit) = init(GuiComponent(), init)
    fun header(init: GuiComponent.() -> Unit) = init(GuiComponent().apply { fontStyle = FontStyle.BOLD; fontSize = 32 }, init)
    fun paragraph(init: GuiComponent.() -> Unit) = init(GuiComponent(), init)

    open fun onRender(action: () -> Unit) {
        val pixelX = x.convert()
        val pixelY = y.convert()
        val pixelWidth = width.convert()
        val pixelHeight = height.convert()

        /* Check to see if blur should be applied */
        if (blur.value > 0) {
            drawBlur(
                pixelX,
                pixelY,
                pixelWidth,
                pixelHeight,
                borderRadius.topLeft.convert(),
                borderRadius.topRight.convert(),
                borderRadius.bottomLeft.convert(),
                borderRadius.bottomRight.convert(),
                blur.convert()
            )
        }

        /* Check to see if component should be rendered */
        if (color.alpha > 0 && width.value > 0 && height.value > 0) {
            drawRectangle(
                pixelX,
                pixelY,
                pixelWidth,
                pixelHeight,
                borderRadius.topLeft.convert(),
                borderRadius.topRight.convert(),
                borderRadius.bottomLeft.convert(),
                borderRadius.bottomRight.convert(),
                color
            )
        }

        /* Check to see if text should be rendered */
        if (text.isNotEmpty()) {
            if (wrapText) {
                fontRenderer.drawWrappedString(
                    text,
                    pixelX,
                    pixelY,
                    pixelWidth,
                    lineSpacing.convert(),
                    color.toRGBA(),
                    fontStyle
                )
            } else {
                fontRenderer.drawString(
                    text,
                    pixelX,
                    pixelY,
                    color.toRGBA(),
                    fontStyle
                )
            }
        }
    }
    open fun onUpdate(action: () -> Unit) {
        fontRenderer = fontManager.getFontRenderer(fontSize)
    }
    open fun onClick(action: () -> Unit) {

    }

    protected fun MeasurementUnit.convert(): Float {
        return when (this) {
            is RemUnit -> this.toPixels(fontSize)
            is PercentUnit -> this.toPixels(width.value) //TODO this is temporary, it should be retrieving the parent's width (obviously)
            is ViewportWidthUnit -> this.toPixels(mc.displayWidth.toFloat())
            is ViewportHeightUnit -> this.toPixels(mc.displayHeight.toFloat())
            is PixelUnit -> this.pixel
        }
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

        backgroundColor = Color.white
        blur = 18.px
        borderRadius = Radius(10.px)

        direction = AlignDirection.COLUMN
        alignment = Alignment.SPACE_BETWEEN

        component {
            width = 10.rem
            height = 10.rem

            backgroundColor = Color.none
            borderRadius.topLeft = 10.px
            borderRadius.topRight = 10.px

            onClick {
                println("Clicked Component!")
            }
        }
    }
}