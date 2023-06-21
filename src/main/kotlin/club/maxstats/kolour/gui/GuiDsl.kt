package club.maxstats.kolour.gui

import club.maxstats.kolour.render.*
import club.maxstats.kolour.util.Color
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11.*

open class ComponentStyle(
    inline protected var style: ComponentStyle.() -> Unit
) {
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

    init {
        this.style()
    }

    fun modify(init: ComponentStyle.() -> Unit): ComponentStyle {
        this.init()
        this.style = init
        return this
    }

    fun clear(): ComponentStyle {
        this.style = {}
        this.style()
        return this
    }

    fun copy(): ComponentStyle {
        val copy = ComponentStyle({})
        copy.style()
        return copy
    }
}

open class AbstractComponent: GuiBuilder()

class GuiComponent: AbstractComponent() {
    val componentList = arrayListOf<GuiBuilder>()

    init {
        parent = this
        rootContainer = this
    }
}

class ReusableComponent(
    val formatting: ReusableComponent.() -> Unit
): AbstractComponent()

sealed class GuiBuilder {
    var style: ComponentStyle = ComponentStyle {}
    var id: String = ""
    internal val children = arrayListOf<AbstractComponent>()
    internal lateinit var parent: GuiBuilder
    internal lateinit var rootContainer: GuiComponent

    /* Computed coordinates in pixels meant to be used for rendering the component */
    internal var compX: Float = 0f
    internal var compY: Float = 0f
    internal var compWidth: Float = 0f
    internal var compHeight: Float = 0f
    protected var mouseX: Float = 0f
    protected var mouseY: Float = 0f

    protected var fontRenderer: FontRenderer = fontManager.getFontRenderer(style.fontSize)

    var onScroll: GuiEvent.() -> Unit = {}
    var onClick: GuiEvent.() -> Unit = {}
    var onUpdate: GuiEvent.() -> Unit = {}
    var onRender: GuiEvent.() -> Unit = {}

    fun onRender(action: GuiEvent.() -> Unit) {
        onRender = action
    }
    fun onUpdate(action: GuiEvent.() -> Unit) {
        onUpdate = action
    }
    fun onClick(action: GuiEvent.() -> Unit) {
        onClick = action
    }
    fun onScroll(action: GuiEvent.() -> Unit) {
        onScroll = action
    }

    fun render(mouseX: Int, mouseY: Int) {
        /* Check to see if blur should be applied */
        if (style.blur.value > 0) {
            drawBlur(
                compX,
                compY,
                compWidth,
                compHeight,
                style.borderRadius.topLeft.convert(),
                style.borderRadius.topRight.convert(),
                style.borderRadius.bottomLeft.convert(),
                style.borderRadius.bottomRight.convert(),
                style.blur.convert()
            )
        }

        /* Check to see if component should be rendered */
        if (style.color.alpha > 0 && compWidth > 0 && compHeight > 0) {
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            drawRectangle(
                compX,
                compY,
                compWidth,
                compHeight,
                style.borderRadius.topLeft.convert(),
                style.borderRadius.topRight.convert(),
                style.borderRadius.bottomLeft.convert(),
                style.borderRadius.bottomRight.convert(),
                style.backgroundColor
            )

            glDisable(GL_BLEND)
        }

        /* Check to see if text should be rendered */
        if (style.text.isNotEmpty()) {
            if (style.wrapText) {
                fontRenderer.drawWrappedString(
                    style.text,
                    compX,
                    compY,
                    compWidth,
                    style.lineSpacing.convert(),
                    style.color.toRGBA(),
                    style.fontStyle
                )
            } else {
                fontRenderer.drawString(
                    style.text,
                    compX,
                    compY,
                    style.color.toRGBA(),
                    style.fontStyle
                )
            }
        }

        // apply translation to onRender so that immediate mode rendering is done within the component
        glPushMatrix()
        glTranslatef(
            compX,
            compY,
            0f
        )

        GuiEvent(
            compX,
            compY,
            compWidth,
            compHeight,
            mouseX,
            mouseY
        ).onRender()

        glPopMatrix()
        children.forEach { it.render(mouseX, mouseY) }
    }
    fun update(mouseX: Int, mouseY: Int, compPosition: ComputedPosition = ComputedPosition(0f, 0f, 0f, 0f)) {
        fontRenderer = fontManager.getFontRenderer(style.fontSize)

        var compPos = compPosition
        // compute position based on minecraft's
        if (rootContainer == this)
            compPos = this.computeRootPosition()

        compX = compPos.x
        compY = compPos.y
        compWidth = compPos.width
        compHeight = compPos.height

        // If width/height hasn't been set or is set as 0, don't attempt to wrap the text (obviously)
        // Instead set the width/height equal to the width/height of the text being rendered
        if (style.text.isNotEmpty()) {
            if (compWidth == 0f)
                style.width = fontRenderer.getWidth(style.text, fontRenderer.getFontFromStyle(style.fontStyle)).px
            if (compHeight == 0f)
                style.height = fontRenderer.getHeight(style.text, fontRenderer.getFontFromStyle(style.fontStyle)).px
        }

        // align and update children
        if (children.isNotEmpty())
            alignChildren(children, style.alignContent, style.alignItems, style.direction, mouseX, mouseY)

        GuiEvent(
            compX,
            compY,
            compWidth,
            compHeight,
            mouseX,
            mouseY
        ).onUpdate()
    }
    fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {
        if (isHovering(mouseX, mouseY))
            GuiEvent(
                compX,
                compY,
                compWidth,
                compHeight,
                mouseX,
                mouseY
            ).onClick()

        children.forEach { it.click(mouseX, mouseY, mouseButton) }
    }
    fun scroll(mouseX: Int, mouseY: Int, scroll: Int) {
        if (isHovering(mouseX, mouseY)) {
            GuiEvent(
                compX,
                compY,
                compWidth,
                compHeight,
                mouseX,
                mouseY
            ).onScroll()
        }

        children.forEach { it.scroll(mouseX, mouseY, scroll) }
    }

    protected fun isHovering(mouseX: Int, mouseY: Int): Boolean {
        return mouseX.toFloat() in compX..(compX + compWidth) && mouseY.toFloat() in compY..(compY + compHeight)
    }
    internal fun MeasurementUnit.convert(): Float {
        return when (this) {
            is EmUnit -> this.toPixels(style.fontSize)
            is RemUnit -> this.toPixels(rootContainer.style.fontSize)
//            is ViewportWidthUnit -> this.toPixels(getScaledResolution().scaledWidth_double.toFloat())
//            is ViewportHeightUnit -> this.toPixels(getScaledResolution().scaledHeight_double.toFloat())
            is PixelUnit -> this.pixel
            is ViewportHeightUnit -> this.toPixels(Display.getHeight().toFloat())
            is ViewportWidthUnit -> this.toPixels(Display.getWidth().toFloat())
        }
    }

    protected open fun init(component: AbstractComponent): AbstractComponent {
        component.parent = this
        component.rootContainer = this.rootContainer

        if (component is ReusableComponent)
            component.formatting(component)

        children += component
        rootContainer.componentList += component

        return component
    }
    open fun style(formatting: ComponentStyle.() -> Unit) {
        val style = ComponentStyle(formatting)
        this.style = style
    }
    open fun component(formatting: AbstractComponent.() -> Unit) {
        this.init(AbstractComponent()).apply(formatting)
    }
    open fun header(formatting: AbstractComponent.() -> Unit) {
        this.init(AbstractComponent()).apply {
            formatting()
            style.fontStyle = FontStyle.BOLD
            style.fontSize = 24
        }
    }
    open fun paragraph(formatting: AbstractComponent.() -> Unit) {
        this.init(AbstractComponent()).apply(formatting)
    }
    operator fun ReusableComponent.unaryPlus(): ReusableComponent {
        val component = ReusableComponent(this.formatting)

        // reusable components should still not reuse component id's
        if (component.id.isNotEmpty())
            component.id = ""

        this@GuiBuilder.init(component)
        return component
    }
    operator fun AbstractComponent.plus(config: AbstractComponent.() -> Unit): AbstractComponent {
        this.config()
        return this
    }
    fun getComponentById(id: String): GuiBuilder? = rootContainer.componentList.find { it.id === id }

    protected fun isRootInitialized(): Boolean {
        return ::rootContainer.isInitialized
    }
}

data class GuiEvent(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val mouseX: Int,
    val mouseY: Int
)

fun gui(init: GuiComponent.() -> Unit): GuiComponent {
    val gui = GuiComponent()
    gui.init()
    gui.componentList += gui
    return gui
}
fun component(init: ReusableComponent.() -> Unit): ReusableComponent {
    return ReusableComponent(init)
}