package club.maxstats.kolour.gui
enum class AlignDirection {
    COLUMN,
    ROW
}
enum class ItemAlignment {
    START,
    MIDDLE,
    END
}
enum class ContentAlignment {
    START,
    MIDDLE,
    END,
    BETWEEN,
    AROUND
}
enum class Position {
    ABSOLUTE,
    RELATIVE,
    STATIC,
    FIXED
}
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
sealed class MeasurementUnit(val value: Float)
data class RemUnit(val rem: Float) : MeasurementUnit(rem) {
    /**
     * Converts the REM (root em) value to pixels based on the specified font size.
     *
     * @param fontSize The font size in pixels.
     * @return The calculated pixel value based on the REM value and font size.
     */
    fun toPixels(fontSize: Int): Float {
        return fontSize * rem
    }
}
data class EmUnit(val em: Float): MeasurementUnit(em) {
    /**
     * Converts the EM value to pixels based on the specified font size.
     *
     * @param fontSize The font size in pixels.
     * @return The calculated pixel value based on the EM value and font size.
     */
    fun toPixels(fontSize: Int): Float {
        return fontSize * em
    }
}
data class ViewportWidthUnit(val vw: Float): MeasurementUnit(vw) {
    /**
     * Converts the viewport width (vw) value to pixels based on the specified viewport width.
     *
     * @param viewWidth The width of the viewport in pixels.
     * @return The calculated pixel value based on the viewport width value.
     */
    fun toPixels(viewWidth: Float): Float {
        return viewWidth * (vw / 100)
    }
}
data class ViewportHeightUnit(val vh: Float): MeasurementUnit(vh) {
    /**
     * Converts the viewport height (vh) value to pixels based on the specified viewport height.
     *
     * @param viewHeight The height of the viewport in pixels.
     * @return The calculated pixel value based on the viewport height value.
     */
    fun toPixels(viewHeight: Float): Float {
        return viewHeight * (vh / 100)
    }
}
data class PixelUnit(val pixel: Float): MeasurementUnit(pixel)

val Number.rem: RemUnit
    get() = RemUnit(this.toFloat())
val Number.em: EmUnit
    get() = EmUnit(this.toFloat())
val Number.vw: ViewportWidthUnit
    get() = ViewportWidthUnit(this.toFloat())
val Number.vh: ViewportHeightUnit
    get() = ViewportHeightUnit(this.toFloat())
val Number.px: PixelUnit
    get() = PixelUnit(this.toFloat())