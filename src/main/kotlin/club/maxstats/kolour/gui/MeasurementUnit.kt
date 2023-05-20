package club.maxstats.kolour.gui

sealed class MeasurementUnit(val value: Float)
data class RemUnit(val rem: Float) : MeasurementUnit(rem) {
    /**
     * Converts the REM (root em) value to pixels based on the specified font size.
     *
     * @param fontSize The font size in pixels.
     * @return The calculated pixel value based on the REM value and font size.
     */
    fun toPixels(fontSize: Float): Float {
        return fontSize * rem
    }
}
data class PercentUnit(val percent: Float): MeasurementUnit(percent) {
    /**
     * Converts the percentage value to pixels based on the specified full value.
     *
     * @param full The pixel amount to calculate the percentage of.
     * @return The calculated pixel value based on the percentage.
     */
    fun toPixels(full: Float): Float {
        return full * (percent / 100)
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
val Number.percent: PercentUnit
    get() = PercentUnit(this.toFloat())
val Number.vw: ViewportWidthUnit
    get() = ViewportWidthUnit(this.toFloat())
val Number.vh: ViewportHeightUnit
    get() = ViewportHeightUnit(this.toFloat())
val Number.px: PixelUnit
    get() = PixelUnit(this.toFloat())