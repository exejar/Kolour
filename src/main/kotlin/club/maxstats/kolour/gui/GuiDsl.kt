package club.maxstats.kolour.gui

import club.maxstats.kolour.Kolour
import club.maxstats.kolour.render.*
import club.maxstats.kolour.util.Color
import club.maxstats.kolour.util.getScaledResolution
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

class GuiScreen: GuiBuilder() {
    init {
        rootContainer = this
        parent = this
    }
}
open class GuiComponent: GuiBuilder()
sealed class GuiBuilder {
    var position: Position = Position.STATIC
    var top: MeasurementUnit? = null
    var right: MeasurementUnit? = null
    var bottom: MeasurementUnit? = null
    var left: MeasurementUnit? = null
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
    var alignContent: ContentAlignment = ContentAlignment.START
    var alignItems: ItemAlignment = ItemAlignment.START

    var blur: MeasurementUnit = 0.px
    var borderRadius: Radius<MeasurementUnit> = Radius(0.px)
    var backgroundColor: Color = Color.none

    protected var onScroll: (() -> Unit)? = null
    protected var onClick: (() -> Unit)? = null
    protected var onUpdate: (() -> Unit)? = null
    protected var onRender: (() -> Unit)? = null

    /* Computed coordinates in pixels meant to be used for rendering the component */
    internal var compX: Float = 0f
    internal var compY: Float = 0f
    internal var compWidth: Float = 0f
    internal var compHeight: Float = 0f

    protected var fontRenderer: FontRenderer = fontManager.getFontRenderer(fontSize)
    protected val children = arrayListOf<GuiComponent>()
    internal lateinit var rootContainer: GuiScreen
    internal lateinit var parent: GuiBuilder

    protected fun <T: GuiComponent>init(component: T, init: T.() -> Unit): T {
        component.rootContainer = this.rootContainer
        component.parent = this
        component.init()
        children.add(component)
        return component
    }
    fun component(init: GuiComponent.() -> Unit) = init(GuiComponent(), init)
    fun header(init: GuiComponent.() -> Unit) = init(GuiComponent().apply { fontStyle = FontStyle.BOLD; fontSize = rootContainer.fontSize * 2 }, init)
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
    fun render(mouseX: Int, mouseY: Int) {
        /* Check to see if blur should be applied */
        if (blur.value > 0) {
            drawBlur(
                compX,
                compY,
                compWidth,
                compHeight,
                borderRadius.topLeft.convert(),
                borderRadius.topRight.convert(),
                borderRadius.bottomLeft.convert(),
                borderRadius.bottomRight.convert(),
                blur.convert()
            )
        }

        /* Check to see if component should be rendered */
        if (color.alpha > 0 && width.value > 0 && height.value > 0) {
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            drawRectangle(
                compX,
                compY,
                compWidth,
                compHeight,
                borderRadius.topLeft.convert(),
                borderRadius.topRight.convert(),
                borderRadius.bottomLeft.convert(),
                borderRadius.bottomRight.convert(),
                backgroundColor
            )

            glDisable(GL_BLEND)
        }

        /* Check to see if text should be rendered */
        if (text.isNotEmpty()) {
            if (wrapText) {
                fontRenderer.drawWrappedString(
                    text,
                    compX,
                    compY,
                    compWidth,
                    lineSpacing.convert(),
                    color.toRGBA(),
                    fontStyle
                )
            } else {
                fontRenderer.drawString(
                    text,
                    compX,
                    compY,
                    color.toRGBA(),
                    fontStyle
                )
            }
        }

        onRender?.invoke()

        children.forEach { it.render(mouseX, mouseY) }
    }
    fun update(mouseX: Int, mouseY: Int, compPosition: ComputedPosition = ComputedPosition(0f, 0f, 0f, 0f)) {
        fontRenderer = fontManager.getFontRenderer(fontSize)

        var compPos = compPosition
        // compute position based on minecraft's resolution
        if (rootContainer == this)
            compPos = this.computeRootPosition()

        compX = compPos.x
        compY = compPos.y
        compWidth = compPos.width
        compHeight = compPos.height

        // align and update children
        if (children.isNotEmpty())
            alignChildren(children, alignContent, alignItems, direction, mouseX, mouseY)

        onUpdate?.invoke()
    }
    internal fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isHovering(mouseX, mouseY))
            onClick?.invoke()

        children.forEach { it.click(mouseX, mouseY, mouseButton) }
    }
    internal fun scroll(mouseX: Int, mouseY: Int, scroll: Int) {
        if (isHovering(mouseX, mouseY))
            onScroll?.invoke()

        children.forEach { it.scroll(mouseX, mouseY, scroll) }
    }

    internal fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in compX..(compX + compWidth) && mouseY.toFloat() in compY..(compY + compHeight)
    }
    internal fun MeasurementUnit.convert(): Float {
        return when (this) {
            is EmUnit -> this.toPixels(fontSize)
            is RemUnit -> this.toPixels(rootContainer.fontSize)
            is ViewportWidthUnit -> this.toPixels(getScaledResolution().scaledWidth_double.toFloat())
            is ViewportHeightUnit -> this.toPixels(getScaledResolution().scaledHeight_double.toFloat())
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
        alignContent = ContentAlignment.BETWEEN

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