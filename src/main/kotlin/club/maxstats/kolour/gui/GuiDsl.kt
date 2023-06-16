package club.maxstats.kolour.gui

import club.maxstats.kolour.Kolour
import club.maxstats.kolour.render.*
import club.maxstats.kolour.util.Color
import club.maxstats.kolour.util.getScaledResolution
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

class GuiScreen: GuiBuilder() {
    val componentList = arrayListOf<GuiBuilder>()
    init {
        rootContainer = this
        parent = this
    }
}
open class GuiComponent(
    inline var formatting: GuiComponent.() -> Unit
): GuiBuilder() {
    fun modify(init: GuiComponent.() -> Unit): GuiComponent {
        this.init()
        this.formatting = init
        return this
    }
}
sealed class GuiBuilder {
    var id: String = ""
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
    var wrapText: Boolean = false

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
    internal val children = arrayListOf<GuiComponent>()
    internal lateinit var rootContainer: GuiScreen
    internal lateinit var parent: GuiBuilder

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
        if (color.alpha > 0 && compWidth > 0 && compHeight > 0) {
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

        // If width/height hasn't been set or is set as 0, don't attempt to wrap the text (obviously)
        // Instead set the width/height equal to the width/height of the text being rendered
        if (text.isNotEmpty()) {
            if (compWidth == 0f)
                width = fontRenderer.getWidth(text, fontRenderer.getFontFromStyle(fontStyle)).px
            if (compHeight == 0f)
                height = fontRenderer.getHeight(text, fontRenderer.getFontFromStyle(fontStyle)).px
        }

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
    fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isHovering(mouseX, mouseY))
            onClick?.invoke()

        children.forEach { it.click(mouseX, mouseY, mouseButton) }
    }
    fun scroll(mouseX: Int, mouseY: Int, scroll: Int) {
        if (isHovering(mouseX, mouseY))
            onScroll?.invoke()

        children.forEach { it.scroll(mouseX, mouseY, scroll) }
    }

    protected fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in compX..(compX + compWidth) && mouseY.toFloat() in compY..(compY + compHeight)
    }
    internal fun MeasurementUnit.convert(): Float {
        return when (this) {
            is EmUnit -> this.toPixels(fontSize)
            is RemUnit -> this.toPixels(rootContainer.fontSize)
            is ViewportWidthUnit -> this.toPixels(getScaledResolution().scaledWidth_double.toFloat())
            is ViewportHeightUnit -> this.toPixels(getScaledResolution().scaledHeight_double.toFloat())
            is PixelUnit -> this.pixel
//            is ViewportHeightUnit -> this.toPixels(Display.getHeight().toFloat())
//            is ViewportWidthUnit -> this.toPixels(Display.getWidth().toFloat())
        }
    }

    protected fun init(component: GuiComponent): GuiComponent {
        component.rootContainer = this.rootContainer
        component.parent = this
        component.formatting(component)
        rootContainer.componentList += component
        children += component
        return component
    }
    fun component(init: GuiComponent.() -> Unit) = this.init(GuiComponent(init))
    fun header(init: GuiComponent.() -> Unit) = this.init(GuiComponent(init)).apply { fontStyle = FontStyle.BOLD; fontSize = 24 }
    fun paragraph(init: GuiComponent.() -> Unit) = this.init(GuiComponent(init))
    operator fun GuiComponent.unaryPlus(): GuiComponent {
        val component = GuiComponent(this.formatting)
        this@GuiBuilder.init(component)
        return component
    }
    fun getComponentById(id: String): GuiBuilder? = rootContainer.componentList.find { it.id === id }
}

fun gui(init: GuiScreen.() -> Unit): GuiScreen {
    val gui = GuiScreen()
    gui.init()
    gui.componentList += gui
    return gui
}
fun component(init: GuiComponent.() -> Unit): GuiComponent {
    val component = GuiComponent(init)
    component.formatting(component)
    return component
}