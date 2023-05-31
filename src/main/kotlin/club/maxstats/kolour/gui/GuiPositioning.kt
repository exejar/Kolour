package club.maxstats.kolour.gui

/**
 *
 */
fun alignChildren(
    children: ArrayList<GuiComponent>,
    alignment: Alignment,
    direction: AlignDirection,
    mouseX: Int,
    mouseY: Int
) {
    when (alignment) {
        Alignment.START -> alignStart(children, direction, mouseX, mouseY)
        Alignment.MIDDLE -> alignMiddle(children, direction, mouseX, mouseY)
        Alignment.END -> alignEnd(children, direction, mouseX, mouseY)
        Alignment.SPACE_BETWEEN -> alignSpaceBetween(children, direction, mouseX, mouseY)
        Alignment.SPACE_APART -> alignSpaceApart(children, direction, mouseX, mouseY)
    }
}

// Root container will always be positioned as if it's static and in a column.
internal fun alignRootContainer(rootContainer: GuiScreen): ComputedPosition {
    // TODO shrink width and height based on margin.right and margin.bottom (only if it over extends minecraft's resolution)
    val compX = with(rootContainer) { margin.left.convert() }
    val compY = with(rootContainer) { margin.top.convert() }
    val compWidth = with(rootContainer) { width.convert() }
    val compHeight = with(rootContainer) { height.convert() }

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
        var compX = 0f
        var compY = 0f
        var compWidth = with(child) { width.convert() }
        var compHeight = with(child) { height.convert() }

        var alignFlow = 0f

        /* Called after the child's x, y, width, and height and component's alignment are computed */
        when (child.position) {
            Position.STATIC -> {     // Position according to the normal flow of GUI
                alignFlow = if (direction == AlignDirection.ROW)
                    with(child) { compX + compWidth + margin.left.convert() + margin.right.convert() }
                else
                    with(child) { compY + compHeight + margin.top.convert() + margin.bottom.convert() }

                compX += currentX
                compY += currentY
            }
            Position.RELATIVE -> {   // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
                alignFlow = if (direction == AlignDirection.ROW)
                    with(child) { compX + compWidth + margin.left.convert() + margin.right.convert() }
                else
                    with(child) { compY + compHeight + margin.top.convert() + margin.bottom.convert() }

                compX += currentX + with(child) { (left?.convert() ?: 0f) - (right?.convert() ?: 0f) }
                compY += currentY + with(child) { (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f) }
            }
            Position.ABSOLUTE -> {   // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
                // TODO not entirely sure if this is how margin is supposed to behave inside FIXED / absolute positioned components
                compX = ancestorX + with(child) { margin.left.convert() + (left?.convert() ?: 0f) }
                compY = ancestorY + with(child) { margin.top.convert() + (top?.convert() ?: 0f) }

                if (compWidth == 0f)
                    compWidth = ancestorWidth - with(child) { (left?.convert() ?: 0f) - (right?.convert() ?: 0f) - margin.right.convert() }
                if (compHeight == 0f)
                    compHeight = ancestorHeight - with(child) { (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f) - margin.bottom.convert() }
            }
            Position.FIXED -> {      // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
                val root = child.rootContainer
                // TODO not entirely sure if this is how margin is supposed to behave inside FIXED / absolute positioned components
                compX = root.compX + with(child) { margin.left.convert() + (left?.convert() ?: 0f) }
                compY = root.compY + with(child) { margin.top.convert() + (top?.convert() ?: 0f) }

                if (compWidth == 0f)
                    compWidth = root.compWidth - with(child) { (left?.convert() ?: 0f) - (right?.convert() ?: 0f) - margin.right.convert() }
                if (compHeight == 0f)
                    compHeight = root.compHeight - with(child) { (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f) - margin.bottom.convert() }
            }
        }

        if (direction == AlignDirection.ROW) {
            currentX += alignFlow
            compX += with(child) { padding.left.convert() }
            compWidth -= with(child) { padding.right.convert() }
        } else {
            currentY += alignFlow
            compY += with(child) { padding.top.convert() }
            compHeight -= with(child) { padding.bottom.convert() }
        }

        val computedPosition = ComputedPosition(
            compX,
            compY,
            compWidth,
            compHeight
        )

        child.update(mouseX, mouseY, computedPosition)
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