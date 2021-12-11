package components

import marki.Window
import marki.renderer.DebugDraw
import org.joml.Vector2f
import org.joml.Vector3f
import util.Settings.GRID_HEIGHT
import util.Settings.GRID_WIDTH
import java.lang.Integer.max

class GridLines : Component() {

    val cameraPos = Window.getScene().camera().position
    val projectionSize = Window.getScene().camera().getProjectionSize()

    val firstX = ((cameraPos.x / GRID_WIDTH).toInt() -1) * GRID_HEIGHT
    val firstY = (cameraPos.y / GRID_HEIGHT).toInt() * GRID_HEIGHT

    var numVtLines = (projectionSize.x / GRID_WIDTH).toInt() + 2
    var numHzLines = (projectionSize.y / GRID_HEIGHT).toInt() + 2

    val height = projectionSize.y.toInt() + GRID_HEIGHT * 2
    val width = projectionSize.x.toInt() + GRID_WIDTH * 2

    val maxLines = max(numVtLines, numHzLines)
    val defaultColor = Vector3f(0.001f, 0.001f, 0.001f)

    override fun imgui() {
        super.imgui()
    }

    override fun update(dt: Float) {
      drawLines()
    }

    private fun drawLines() {
        for (i in 0 until maxLines) {
            val x = firstX + GRID_WIDTH * i
            val y = firstY + GRID_HEIGHT * i
            if (i < numVtLines) {
                DebugDraw.addLine2D(Vector2f(x.toFloat(), firstY.toFloat()), Vector2f(x.toFloat(), firstY.toFloat() + height), defaultColor, 2)
            }
            if (i < numHzLines) {
                DebugDraw.addLine2D(Vector2f(firstX.toFloat(), y.toFloat()), Vector2f(firstX.toFloat() + width, y.toFloat()), defaultColor, 2)
            }
        }
    }
}