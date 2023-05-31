package club.maxstats.kolour.gui

/**
 *
 */
fun alignChildren(
    children: ArrayList<GuiComponent>,
    alignItems: ItemAlignment,
    alignContent: ContentAlignment,
    direction: AlignDirection,
    mouseX: Int,
    mouseY: Int
) {
    when (alignItems) {
        ItemAlignment.START -> alignStart(children, direction, mouseX, mouseY)
        ItemAlignment.MIDDLE -> alignMiddle(children, direction, mouseX, mouseY)
        ItemAlignment.END -> alignEnd(children, direction, mouseX, mouseY)
        ItemAlignment.SPACE_BETWEEN -> alignSpaceBetween(children, direction, mouseX, mouseY)
        ItemAlignment.SPACE_APART -> alignSpaceApart(children, direction, mouseX, mouseY)
    }
}

// Root container will always be positioned as if it's static and in a column.

internal fun GuiScreen.computeRootPosition(): ComputedPosition {
    val compX = margin.left.convert()
    val compY = margin.top.convert()
    val compWidth = width.convert()
    val compHeight = height.convert()

    return ComputedPosition(compX, compY, compWidth, compHeight)
}

private fun alignStart(children: ArrayList<GuiComponent>, direction: AlignDirection, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first().parent
    val positionedAncestor = findPositionedAncestor(currentComponent)

    // computed positions already have component's padding applied to them
    val ancestorX = positionedAncestor.compX
    val ancestorY = positionedAncestor.compY
    val ancestorWidth = positionedAncestor.compWidth
    val ancestorHeight = positionedAncestor.compHeight

    var currentX = ancestorX
    var currentY = ancestorY

    for (child in children) {
        child.run {
            var computedX = margin.left.convert()
            var computedY = margin.top.convert()
            var computedWidth = width.convert()
            var computedHeight = height.convert()

            var alignFlow = 0f

            /* Called after the child's x, y, width, and height and component's alignment are computed */
            when (child.position) {
                Position.STATIC -> {     // Position according to the normal flow of GUI
                    alignFlow = if (direction == AlignDirection.ROW)
                        computedX + computedWidth + margin.right.convert()
                    else
                        computedY + computedHeight + margin.bottom.convert()

                    computedX += currentX
                    computedY += currentY
                }

                Position.RELATIVE -> {   // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
                    alignFlow = if (direction == AlignDirection.ROW)
                        computedX + computedWidth + margin.right.convert()
                    else
                        computedY + computedHeight + margin.bottom.convert()

                    computedX += currentX + (left?.convert() ?: 0f) - (right?.convert() ?: 0f)
                    computedY += currentY + (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f)
                }

                Position.ABSOLUTE -> {   // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
                    // TODO not entirely sure if this is how margin is supposed to behave inside FIXED / absolute positioned components
                    computedX = ancestorX + (left?.convert() ?: 0f)
                    computedY = ancestorY + (top?.convert() ?: 0f)

                    if (computedWidth == 0f)
                        computedWidth = ancestorWidth - (left?.convert() ?: 0f) - (right?.convert() ?: 0f) - margin.right.convert()
                    if (computedHeight == 0f)
                        computedHeight = ancestorHeight - (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f) - margin.bottom.convert()

                }

                Position.FIXED -> {      // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
                    val root = child.rootContainer
                    // TODO not entirely sure if this is how margin is supposed to behave inside FIXED / absolute positioned components
                    computedX = root.compX + (left?.convert() ?: 0f)
                    computedY = root.compY + (top?.convert() ?: 0f)

                    if (computedWidth == 0f)
                        computedWidth = root.compWidth - (left?.convert() ?: 0f) - (right?.convert() ?: 0f) - margin.right.convert()
                    if (computedHeight == 0f)
                        computedHeight = root.compHeight - (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f) - margin.bottom.convert()
                }
            }

            // apply padding
            positionedAncestor.run {
                computedX += padding.left.convert()
                computedY += padding.top.convert()

                val ancestorMaxX = compX + compWidth - padding.right.convert()
                val ancestorMaxY = compY + compHeight - padding.bottom.convert()

                val xRemainder = computedX + computedWidth - ancestorMaxX
                val yRemainder = computedY + computedHeight - ancestorMaxY

                if (xRemainder > 0)
                    computedX -= xRemainder
                if (yRemainder > 0)
                    computedY -= yRemainder

                if (direction == AlignDirection.ROW)
                    currentX += alignFlow
                else
                    currentY += alignFlow

                val computedPosition = ComputedPosition(
                    computedX,
                    computedY,
                    computedWidth,
                    computedHeight
                )

                child.update(mouseX, mouseY, computedPosition)
            }
        }
    }
}

fun findPositionedAncestor(component: GuiBuilder): GuiBuilder {
    val parent = component.parent

    if (parent.position != Position.STATIC || parent == component.rootContainer)
        return parent

    return findPositionedAncestor(component)
}

private fun alignMiddle(children: ArrayList<GuiComponent>, direction: AlignDirection, mouseX: Int, mouseY: Int) {
    var currentX = 0f
    var currentY = 0f

    for (child in children) {
        var compX = 0f
        var compY = 0f
        var compWidth = 0f
        var compHeight = 0f

        var flowPosition = 0f
        var flowSize = 0f



        /* Called after the child's x, y, width, and height and component's alignment are computed */
        when (child.position) {
            Position.STATIC -> println()   // Position according to the normal flow of GUI
            Position.RELATIVE -> println() // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
            Position.ABSOLUTE -> println() // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
            Position.FIXED -> println()    // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
        }

        if (direction == AlignDirection.ROW) {
            currentX += compX + compWidth
        } else {
            currentY += compY + compHeight
        }

        val computedPosition = ComputedPosition(
            currentX,
            currentY,
            compWidth,
            compHeight
        )

        child.update(mouseX, mouseY, computedPosition)
    }
}

private fun alignEnd(children: ArrayList<GuiComponent>, direction: AlignDirection, mouseX: Int, mouseY: Int) {
    var currentX = 0f
    var currentY = 0f

    for (child in children) {
        var compX = 0f
        var compY = 0f
        var compWidth = 0f
        var compHeight = 0f

        var flowPosition = 0f
        var flowSize = 0f



        /* Called after the child's x, y, width, and height and component's alignment are computed */
        when (child.position) {
            Position.STATIC -> println()   // Position according to the normal flow of GUI
            Position.RELATIVE -> println() // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
            Position.ABSOLUTE -> println() // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
            Position.FIXED -> println()    // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
        }

        if (direction == AlignDirection.ROW) {
            currentX += compX + compWidth
        } else {
            currentY += compY + compHeight
        }

        val computedPosition = ComputedPosition(
            currentX,
            currentY,
            compWidth,
            compHeight
        )

        child.update(mouseX, mouseY, computedPosition)
    }
}

private fun alignSpaceBetween(children: ArrayList<GuiComponent>, direction: AlignDirection, mouseX: Int, mouseY: Int) {
    var currentX = 0f
    var currentY = 0f

    for (child in children) {
        var compX = 0f
        var compY = 0f
        var compWidth = 0f
        var compHeight = 0f

        var flowPosition = 0f
        var flowSize = 0f



        /* Called after the child's x, y, width, and height and component's alignment are computed */
        when (child.position) {
            Position.STATIC -> println()   // Position according to the normal flow of GUI
            Position.RELATIVE -> println() // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
            Position.ABSOLUTE -> println() // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
            Position.FIXED -> println()    // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
        }

        if (direction == AlignDirection.ROW) {
            currentX += compX + compWidth
        } else {
            currentY += compY + compHeight
        }

        val computedPosition = ComputedPosition(
            currentX,
            currentY,
            compWidth,
            compHeight
        )

        child.update(mouseX, mouseY, computedPosition)
    }
}

private fun alignSpaceApart(children: ArrayList<GuiComponent>, direction: AlignDirection, mouseX: Int, mouseY: Int) {
    var currentX = 0f
    var currentY = 0f

    for (child in children) {
        var compX = 0f
        var compY = 0f
        var compWidth = 0f
        var compHeight = 0f

        var flowPosition = 0f
        var flowSize = 0f



        /* Called after the child's x, y, width, and height and component's alignment are computed */
        when (child.position) {
            Position.STATIC -> println()   // Position according to the normal flow of GUI
            Position.RELATIVE -> println() // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
            Position.ABSOLUTE -> println() // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
            Position.FIXED -> println()    // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
        }

        if (direction == AlignDirection.ROW) {
            currentX += compX + compWidth
        } else {
            currentY += compY + compHeight
        }

        val computedPosition = ComputedPosition(
            currentX,
            currentY,
            compWidth,
            compHeight
        )

        child.update(mouseX, mouseY, computedPosition)
    }
}

data class ComputedPosition(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)