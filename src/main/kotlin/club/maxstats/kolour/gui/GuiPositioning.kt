package club.maxstats.kolour.gui

/**
 *
 */
fun alignChildren(
    children: ArrayList<AbstractComponent>,
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
internal fun AbstractComponent.computeRootPosition(): ComputedPosition {
    val compX = style.margin.left.convert()
    val compY = style.margin.top.convert()
    val compWidth = style.width.convert()
    val compHeight = style.height.convert()

    return ComputedPosition(compX, compY, compWidth, compHeight)
}

/**
 * @return computed content position for child
 */
private fun AbstractComponent.alignItems(alignment: ItemAlignment, direction: AlignDirection, computedPosition: ComputedPosition) {
    val positionedAncestor = findPositionedAncestor()
    when (alignment) {
        ItemAlignment.MIDDLE -> {
            if (direction == AlignDirection.COLUMN)
                computedPosition.x = positionedAncestor.style.padding.left.convert() + (positionedAncestor.compWidth / 2) - (computedPosition.width / 2) - positionedAncestor.style.padding.right.convert()
            else
                computedPosition.y = positionedAncestor.style.padding.top.convert() + (positionedAncestor.compHeight / 2) - (computedPosition.height / 2) - positionedAncestor.style.padding.bottom.convert()
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

private fun ArrayList<AbstractComponent>.alignContent(
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
            var computedX = style.margin.left.convert()
            var computedY = style.margin.top.convert()
            var computedWidth = style.width.convert()
            var computedHeight = style.height.convert()

            var computedPosition = ComputedPosition(
                computedX,
                computedY,
                computedWidth,
                computedHeight
            )

            // Align content before layout computation.
            // Content alignment should only be applied to non-fixed/absolute components
            if (style.position != Position.FIXED && style.position != Position.ABSOLUTE) {
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
            when (style.position) {
                Position.STATIC -> {     // Position according to the normal flow of GUI
                    alignFlow += if (direction == AlignDirection.ROW)
                        computedX + computedWidth + style.margin.right.convert()
                    else
                        computedY + computedHeight + style.margin.bottom.convert()

                    computedX += currentX
                    computedY += currentY
                }

                Position.RELATIVE -> {   // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
                    alignFlow += if (direction == AlignDirection.ROW)
                        computedX + computedWidth + style.margin.right.convert()
                    else
                        computedY + computedHeight + style.margin.bottom.convert()

                    computedX += currentX + (style.left?.convert() ?: 0f) - (style.right?.convert() ?: 0f)
                    computedY += currentY + (style.top?.convert() ?: 0f) - (style.bottom?.convert() ?: 0f)
                }

                Position.ABSOLUTE -> {   // Position relative to nearest positioned ancestor (Instead of positioned relative to the viewport, like fixed positioning)
                    val left = style.left?.convert() ?: 0f
                    val right = style.right?.convert() ?: 0f
                    val top = style.top?.convert() ?: 0f
                    val bottom = style.bottom?.convert() ?: 0f

                    if (left > 0)
                        computedX = ancestorX + left
                    else if (right > 0)
                        computedX = ancestorX + ancestorWidth - right - computedWidth

                    if (top > 0)
                        computedY = ancestorY + top
                    else if (bottom > 0)
                        computedX = ancestorY + ancestorHeight - bottom - computedHeight


                    if (computedWidth == 0f)
                        computedWidth = ancestorWidth - left - right - style.margin.right.convert()
                    if (computedHeight == 0f)
                        computedHeight = ancestorHeight - top - bottom - style.margin.bottom.convert()

                }

                Position.FIXED -> {      // Position relative to the viewport. Use top, right, bottom, and left properties to position the component
                    val root = child.rootContainer

                    val left = style.left?.convert() ?: 0f
                    val right = style.right?.convert() ?: 0f
                    val top = style.top?.convert() ?: 0f
                    val bottom = style.bottom?.convert() ?: 0f

                    if (left > 0)
                        computedX = root.compX + left
                    else if (right > 0)
                        computedX = root.compX + root.compWidth - right - computedWidth

                    if (top > 0)
                        computedY = root.compY + top
                    else if (bottom > 0)
                        computedX = root.compY + root.compHeight - bottom - computedHeight

                    if (computedWidth == 0f)
                        computedWidth = root.compWidth - left - right - style.margin.right.convert()
                    if (computedHeight == 0f)
                        computedHeight = root.compHeight - top - bottom - style.margin.bottom.convert()
                }
            }

            ancestor.run {
                // only apply padding to children that are outside the padded area
                val ancestorMinX = compX + style.padding.left.convert()
                val ancestorMinY = compY + style.padding.top.convert()
                val ancestorMaxX = compX + compWidth - style.padding.right.convert()
                val ancestorMaxY = compY + compHeight - style.padding.bottom.convert()

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

private fun contentStart(children: ArrayList<AbstractComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
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

private fun contentMiddle(children: ArrayList<AbstractComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
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

private fun contentEnd(children: ArrayList<AbstractComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
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

private fun contentBetween(children: ArrayList<AbstractComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
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

private fun contentAround(children: ArrayList<AbstractComponent>, direction: AlignDirection, alignItems: ItemAlignment, mouseX: Int, mouseY: Int) {
    val currentComponent = children.first()
    val positionedAncestor = currentComponent.findPositionedAncestor()

    val ancestorX = positionedAncestor.compX
    val ancestorY = positionedAncestor.compY
    val ancestorWidth = positionedAncestor.compWidth
    val ancestorHeight = positionedAncestor.compHeight

    val combinedLength = children.combinedLength()
    val contentSpacer = if (direction == AlignDirection.ROW)
        (ancestorWidth - combinedLength.first) / (children.size + 1)
    else
        (ancestorHeight - combinedLength.second) / (children.size + 1)

    children.alignContent(
        direction,
        alignItems,
        positionedAncestor,
        ancestorX + contentSpacer,
        ancestorY + contentSpacer,
        contentSpacer,
        mouseX,
        mouseY
    )
}

fun GuiBuilder.findPositionedAncestor(): GuiBuilder {
    val parent = parent

    if (parent.style.position != Position.STATIC || parent == rootContainer)
        return parent

    return parent.findPositionedAncestor()
}

fun ArrayList<AbstractComponent>.combinedLength(): Pair<Float, Float> {
    var contentWidth = 0f
    var contentHeight = 0f
    var longest = 0f

    // compute full size of all children
    for (child in this) {
        child.run {
            var computedX = style.margin.left.convert()
            var computedY = style.margin.top.convert()
            val computedWidth = style.width.convert()
            val computedHeight = style.height.convert()

            var alignFlow = 0f

            /* Called after the child's x, y, width, and height and component's alignment are computed */
            when (child.style.position) {
                Position.STATIC -> {     // Position according to the normal flow of GUI
                    val length: Float

                    if (parent.style.direction == AlignDirection.ROW) {
                        length = style.margin.left.convert() + computedWidth + style.margin.right.convert()
                        alignFlow = computedX + length
                    } else {
                        length = style.margin.top.convert() + computedHeight + style.margin.bottom.convert()
                        alignFlow = computedY + length
                    }

                    if (longest < length)
                        longest = length

                    computedX += contentWidth
                    computedY += contentHeight
                }

                Position.RELATIVE -> {   // Position relative to its normal position, take into account of top, right, bottom, left (these values do not modify the flow of siblings)
                    val length: Float

                    if (parent.style.direction == AlignDirection.ROW) {
                        length = style.margin.left.convert() + computedWidth + style.margin.right.convert()
                        alignFlow = computedX + length
                    } else {
                        length = style.margin.top.convert() + computedHeight + style.margin.bottom.convert()
                        alignFlow = computedY + length
                    }

                    if (longest < length)
                        longest = length

                    computedX += contentWidth + (style.left?.convert() ?: 0f) - (style.right?.convert() ?: 0f)
                    computedY += contentHeight + (style.top?.convert() ?: 0f) - (style.bottom?.convert() ?: 0f)
                }

                else -> {
                    // nothing
                }
            }

            if (parent.style.direction === AlignDirection.ROW) {
                contentWidth += alignFlow
                contentHeight = longest
            } else {
                contentHeight += alignFlow
                contentWidth = longest
            }
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