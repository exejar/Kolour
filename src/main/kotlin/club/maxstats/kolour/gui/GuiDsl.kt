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
class GuiText: GuiComponent() {
    var text: String = ""
    var style: FontStyle = FontStyle.PLAIN
    var lineSpacing: Float = 0f
    var wrapText: Boolean = true
    private var fontRenderer: FontRenderer = fontManager.getFontRenderer(fontSize)
    override fun onRender(action: () -> Unit) {
        val pixelX = x.convert()
        val pixelY = y.convert()

        if (wrapText) {
            val pixelWidth = width.convert()

            fontRenderer.drawWrappedString(
                text,
                pixelX,
                pixelY,
                pixelWidth,
                lineSpacing,
                color.toRGBA(),
                style
            )
        }
        else {
            fontRenderer.drawString(
                text,
                pixelX,
                pixelY,
                color.toRGBA(),
                style
            )
        }
    }

    override fun onUpdate(action: () -> Unit) {
        fontRenderer = fontManager.getFontRenderer(fontSize)
    }
}
sealed class GuiBuilder {
    protected val children = arrayListOf<GuiBuilder>()

    var color: Color = Color.none
    var x: MeasurementUnit = 0.px
    var y: MeasurementUnit = 0.px
    var width: MeasurementUnit = 0.px
    var height: MeasurementUnit = 0.px
    var position: Position = Position.STATIC
    var fontSize: Int = 16
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
    fun header(init: GuiText.() -> Unit) = init(GuiText().apply { style = FontStyle.BOLD; fontSize = 32 }, init)
    fun paragraph(init: GuiText.() -> Unit) = init(GuiText(), init)

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
    }
    open fun onUpdate(action: () -> Unit) {

    }
    fun onClick(action: () -> Unit) {

    }

    protected fun MeasurementUnit.convert(): Float {
        return when (this) {
            is RemUnit -> this.toPixels(fontSize)
            is PercentUnit -> this.toPixels(width.value) // this is temporary, it should be retrieving the parent's width (obviously)
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