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
        // rem calculation
        return rem
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
        // percent calculation
        return percent
    }
}
data class PixelUnit(val pixel: Float): MeasurementUnit(pixel)

val Number.rem: RemUnit
    get() = RemUnit(this.toFloat())

val Number.percent: PercentUnit
    get() = PercentUnit(this.toFloat())

val Number.px: PixelUnit
    get() = PixelUnit(this.toFloat())