package club.maxstats.kolour.gui

/**
 *
 */
fun alignChildren(
    children: ArrayList<GuiComponent>,
    alignContent: ContentAlignment,
    alignItems: ItemAlignment,
    direction: AlignDirection,
    mouseX: Int,
    mouseY: Int
) {
    when (alignContent) {
        ContentAlignment.START -> contentStart(children, direction, alignItems, mouseX, mouseY)
        ContentAlignment.MIDDLE -> contentMiddle(children, direction, alignItems, mouseX, mouseY)
        ContentAlignment.END -> contentEnd(children, direction, alignItems, mouseX, mouseY)
        ContentAlignment.BETWEEN -> contentBetween(children, direction, alignItems, mouseX, mouseY)
        ContentAlignment.AROUND -> contentAround(children, direction, alignItems, mouseX, mouseY)
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

/**
 * @return computed content position for child
 */
private fun GuiComponent.alignItems(alignment: ItemAlignment, direction: AlignDirection, computedPosition: ComputedPosition) {
    val positionedAncestor = findPositionedAncestor()
    when (alignment) {
        ItemAlignment.MIDDLE -> {
            if (direction == AlignDirection.COLUMN)
                computedPosition.x = positionedAncestor.padding.left.convert() + (positionedAncestor.compWidth / 2) - (computedPosition.width / 2) - positionedAncestor.padding.right.convert()
            else
                computedPosition.y = positionedAncestor.padding.top.convert() + (positionedAncestor.compHeight / 2) - (computedPosition.height / 2) - positionedAncestor.padding.bottom.convert()
        }
        ItemAlignment.END -> {
            if (direction == AlignDirection.COLUMN)
                computedPosition.x = positionedAncestor.compWidth - computedPosition.width
            else
                computedPosition.y = positionedAncestor.compHeight - computedPosition.height
        }
        ItemAlignment.START -> {
            // do nothing
        }
    }
}

private fun ArrayList<GuiComponent>.alignContent(
    direction: AlignDirection,
    alignItems: ItemAlignment,
    ancestor: GuiBuilder,
    startX: Float,
    startY: Float,
    alignSpacing: Float,
    mouseX: Int,
    mouseY: Int
) {
    val ancestorX = ancestor.compX
    val ancestorY = ancestor.compY
    val ancestorWidth = ancestor.compWidth
    val ancestorHeight = ancestor.compHeight

    var currentX = startX
    var currentY = startY

    for (child in this) {
        child.run {
            var computedX = margin.left.convert()
            var computedY = margin.top.convert()
            var computedWidth = width.convert()
            var computedHeight = height.convert()

            var computedPosition = ComputedPosition(
                computedX,
                computedY,
                computedWidth,
                computedHeight
            )

            // Align content before layout computation.
            // Content alignment should only be applied to non-fixed/absolute components
            if (child.position != Position.FIXED && child.position != Position.ABSOLUTE) {
                alignItems(
                    alignItems,
                    direction,
                    computedPosition
                )
                computedX = computedPosition.x
                computedY = computedPosition.y
            }

            var alignFlow = alignSpacing

            /* Called after the child's x, y, width, and height and component's alignment are computed */
            when (child.position) {
                Position.STATIC -> {     // Position according to the normal flow of GUI
                    alignFlow += if (direction == AlignDirection.ROW)
                        computedX + computedWidth + margin.right.convert()
                    else
                        computedY + computedHeight + margin.bottom.convert()

                    computedX += currentX
                    computedY += currentY
                }

                Position.RELATIVE -> {   // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
                    alignFlow += if (direction == AlignDirection.ROW)
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

            ancestor.run {
                // only apply padding to children that are outside the padded area
                val ancestorMinX = compX + padding.left.convert()
                val ancestorMinY = compY + padding.top.convert()
                val ancestorMaxX = compX + compWidth - padding.right.convert()
                val ancestorMaxY = compY + compHeight - padding.bottom.convert()

                val leftRemainder = ancestorMinX - computedX
                val topRemainder = ancestorMinY - computedY
                val rightRemainder = computedX + computedWidth - ancestorMaxX
                val bottomRemainder = computedY + computedHeight - ancestorMaxY

                if (leftRemainder > 0)
                    computedX += leftRemainder
                if (topRemainder > 0)
                    computedY += topRemainder
                if (rightRemainder > 0)
                    computedX -= rightRemainder
                if (bottomRemainder > 0)
                    computedY -= bottomRemainder

                if (direction == AlignDirection.ROW)
                    currentX += alignFlow
                else
                    currentY += alignFlow

                computedPosition = ComputedPosition(
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

private fun contentStart(children: ArrayList<GuiComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first()
    val positionedAncestor = currentComponent.findPositionedAncestor()

    children.alignContent(
        direction,
        alignItems,
        positionedAncestor,
        positionedAncestor.compX,
        positionedAncestor.compY,
        0f,
        mouseX,
        mouseY
    )
}

private fun contentMiddle(children: ArrayList<GuiComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first()
    val positionedAncestor = currentComponent.findPositionedAncestor()

    val ancestorX = positionedAncestor.compX
    val ancestorY = positionedAncestor.compY
    val ancestorWidth = positionedAncestor.compWidth
    val ancestorHeight = positionedAncestor.compHeight

    val combinedLength = children.combinedLength()
    val currentX = if (direction == AlignDirection.ROW)
        ancestorX + (ancestorWidth / 2 - combinedLength.first / 2)
    else
        ancestorX

    val currentY = if (direction == AlignDirection.COLUMN)
        ancestorY + (ancestorHeight / 2 - combinedLength.second / 2)
    else
        ancestorY

    children.alignContent(
        direction,
        alignItems,
        positionedAncestor,
        currentX,
        currentY,
        0f,
        mouseX,
        mouseY
    )
}

private fun contentEnd(children: ArrayList<GuiComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first()
    val positionedAncestor = currentComponent.findPositionedAncestor()

    val ancestorX = positionedAncestor.compX
    val ancestorY = positionedAncestor.compY
    val ancestorWidth = positionedAncestor.compWidth
    val ancestorHeight = positionedAncestor.compHeight

    val combinedLength = children.combinedLength()
    val currentX = if (direction == AlignDirection.ROW)
        ancestorX + ancestorWidth - combinedLength.first
    else
        ancestorX

    val currentY = if (direction == AlignDirection.COLUMN)
        ancestorY + ancestorHeight - combinedLength.second
    else
        ancestorY

    children.alignContent(
        direction,
        alignItems,
        positionedAncestor,
        currentX,
        currentY,
        0f,
        mouseX,
        mouseY
    )
}

private fun contentBetween(children: ArrayList<GuiComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first()
    val positionedAncestor = currentComponent.findPositionedAncestor()

    val ancestorX = positionedAncestor.compX
    val ancestorY = positionedAncestor.compY
    val ancestorWidth = positionedAncestor.compWidth
    val ancestorHeight = positionedAncestor.compHeight

    val combinedLength = children.combinedLength()
    val contentSpacer = if (direction == AlignDirection.ROW)
        (ancestorWidth - combinedLength.first) / (children.size - 1)
    else
        (ancestorHeight - combinedLength.second) / (children.size - 1)

    children.alignContent(
        direction,
        alignItems,
        positionedAncestor,
        ancestorX,
        ancestorY,
        contentSpacer,
        mouseX,
        mouseY
    )
}

private fun contentAround(children: ArrayList<GuiComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first()
    val positionedAncestor = currentComponent.findPositionedAncestor()

    val ancestorX = positionedAncestor.compX
    val ancestorY = positionedAncestor.compY
    val ancestorWidth = positionedAncestor.compWidth
    val ancestorHeight = positionedAncestor.compHeight

    val combinedLength = children.combinedLength()
    val contentSpacer = if (direction == AlignDirection.ROW)
        (ancestorWidth - combinedLength.first) / (children.size)
    else
        (ancestorHeight - combinedLength.second) / (children.size)

    children.alignContent(
        direction,
        alignItems,
        positionedAncestor,
        ancestorX + contentSpacer / 2,
        ancestorY + contentSpacer / 2,
        contentSpacer,
        mouseX,
        mouseY
    )
}

fun GuiBuilder.findPositionedAncestor(): GuiBuilder {
    val parent = parent

    if (parent.position != Position.STATIC || parent == rootContainer)
        return parent

    return parent.findPositionedAncestor()
}

fun ArrayList<GuiComponent>.combinedLength(): Pair<Float, Float> {
    var contentWidth = 0f
    var contentHeight = 0f

    // compute full size of all children
    for (child in this) {
        child.run {
            var computedX = margin.left.convert()
            var computedY = margin.top.convert()
            val computedWidth = width.convert()
            val computedHeight = height.convert()

            var alignFlow = 0f

            /* Called after the child's x, y, width, and height and component's alignment are computed */
            when (child.position) {
                Position.STATIC -> {     // Position according to the normal flow of GUI
                    alignFlow = if (direction == AlignDirection.ROW)
                        computedX + computedWidth + margin.right.convert()
                    else
                        computedY + computedHeight + margin.bottom.convert()

                    computedX += contentWidth
                    computedY += contentHeight
                }

                Position.RELATIVE -> {   // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
                    alignFlow = if (direction == AlignDirection.ROW)
                        computedX + computedWidth + margin.right.convert()
                    else
                        computedY + computedHeight + margin.bottom.convert()

                    computedX += contentWidth + (left?.convert() ?: 0f) - (right?.convert() ?: 0f)
                    computedY += contentHeight + (top?.convert() ?: 0f) - (bottom?.convert() ?: 0f)
                }

                else -> {
                    // nothing
                }
            }

            if (direction == AlignDirection.ROW)
                contentWidth += alignFlow
            else
                contentHeight += alignFlow
        }
    }

    return contentWidth to contentHeight
}

data class ComputedPosition(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
)