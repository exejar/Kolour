package club.maxstats.kolour.gui

import club.maxstats.kolour.render.*
import club.maxstats.kolour.util.Color
import club.maxstats.kolour.util.mc

class GuiScreen: GuiBuilder() {
    init {
        rootContainer = this
        parent = this
    }
}
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

    protected var onScroll: (() -> Unit)? = null
    protected var onClick: (() -> Unit)? = null
    protected var onUpdate: (() -> Unit)? = null
    protected var onRender: (() -> Unit)? = null

    protected var fontRenderer: FontRenderer = fontManager.getFontRenderer(fontSize)
    protected val children = arrayListOf<GuiBuilder>()
    protected lateinit var rootContainer: GuiScreen
    protected lateinit var parent: GuiBuilder

    protected fun <T: GuiBuilder>init(component: T, init: T.() -> Unit): T {
        component.rootContainer = this.rootContainer
        component.parent = this
        component.init()
        children.add(component)
        return component
    }
    fun component(init: GuiComponent.() -> Unit) = init(GuiComponent(), init)
    fun header(init: GuiComponent.() -> Unit) = init(GuiComponent().apply { fontStyle = FontStyle.BOLD; fontSize = 32 }, init)
    fun paragraph(init: GuiComponent.() -> Unit) = init(GuiComponent(), init)

    fun onRender(action: () -> Unit) {
        onRender = action
    }
    fun onUpdate(action: () -> Unit) {
        onUpdate = action
    }
    fun onClick(action: () -> Unit) {
        onClick = action
    }
    fun onScroll(action: () -> Unit) {
        onScroll = action
    }
    protected fun render(mouseX: Int, mouseY: Int) {
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
                backgroundColor
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

        onRender?.invoke()
    }
    protected fun update(mouseX: Int, mouseY: Int) {
        fontRenderer = fontManager.getFontRenderer(fontSize)
        onUpdate?.invoke()
    }
    protected fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isHovering(mouseX, mouseY))
            onClick?.invoke()

        children.forEach { it.click(mouseX, mouseY, mouseButton) }
    }
    protected fun scroll(mouseX: Int, mouseY: Int, scroll: Int) {
        if (isHovering(mouseX, mouseY))
            onScroll?.invoke()

        children.forEach { it.scroll(mouseX, mouseY, scroll) }
    }

    protected fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        val pixelX = x.convert()
        val pixelY = y.convert()
        val pixelWidth = width.convert()
        val pixelHeight = height.convert()

        return mouseX.toFloat() in pixelX..(pixelX + pixelWidth) && mouseY.toFloat() in pixelY..(pixelY + pixelHeight)
    }
    protected fun MeasurementUnit.convert(): Float {
        return when (this) {
            is EmUnit -> this.toPixels(fontSize)
            is RemUnit -> this.toPixels(rootContainer.fontSize)
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
        blur = 1.125.rem
        borderRadius = Radius(10.px)

        direction = AlignDirection.COLUMN
        alignment = Alignment.SPACE_BETWEEN

        component {
            text = "Click Me!"
            width = 10.rem
            height = 10.rem

            backgroundColor = Color(0, 150, 0, 255)
            borderRadius.topLeft = 10.px
            borderRadius.topRight = 10.px

            onClick {
                text = "You Rule!"
            }
        }
        component {
            text = "Don't Click Me!"
            width = 10.rem
            height = 10.rem

            backgroundColor = Color(150, 0, 0, 255)
            borderRadius.bottomLeft = 10.px
            borderRadius.bottomRight = 10.px

            onClick {
                text = "You Suck!"
            }
        }
    }
}