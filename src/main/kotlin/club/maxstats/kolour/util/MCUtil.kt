package club.maxstats.kolour.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution

val mc = Minecraft.getMinecraft()
fun getScaledResolution(): ScaledResolution = ScaledResolution(mc)

fun calculateScaleFactor(mc: Minecraft): Int {
    val displayWidth = mc.displayWidth
    val displayHeight = mc.displayHeight

    var scaleFactor = 1
    val isUnicode = mc.isUnicode

    val guiScale = mc.gameSettings.guiScale
    if (guiScale == 0) scaleFactor = 1000

    while (scaleFactor < guiScale && displayWidth / (scaleFactor + 1) >= 320 && displayHeight / (scaleFactor + 1) >= 240) ++scaleFactor
    if (isUnicode && scaleFactor % 2 != 0 && scaleFactor != 1) --scaleFactor

    return scaleFactor
}