package components

import marki.Window
import marki.renderer.DebugDraw
import org.joml.Vector2f
import org.joml.Vector3f
import util.Settings.GRID_HEIGHT
import util.Settings.GRID_WIDTH
import java.lang.Integer.max

class GridLines : Component() {

    val camera = Window.getScene().camera
    val cameraPos = camera.position
    val projectionSize = camera.getProjectionSize()

    val defaultColor = Vector3f(0.001f, 0.001f, 0.001f)

    override fun imgui() {
        super.imgui()
    }

    override fun editorUpdate(dt: Float) {
      drawLines()
    }

    private fun drawLines() {
        val firstX = ((cameraPos.x / GRID_WIDTH).toInt() -1) * GRID_HEIGHT
        val firstY = (cameraPos.y / GRID_HEIGHT).toInt() * GRID_HEIGHT

        val numVtLines = (projectionSize.x * camera.zoom / GRID_WIDTH).toInt() + 2
        val numHzLines = (projectionSize.y * camera.zoom  / GRID_HEIGHT).toInt() + 2

        val height = (projectionSize.y * camera.zoom).toInt() + GRID_HEIGHT * 2
        val width = (projectionSize.x * camera.zoom).toInt() + GRID_WIDTH * 2

        val maxLines = max(numVtLines, numHzLines)

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