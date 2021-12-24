package components

import marki.Window
import marki.renderer.DebugDraw
import org.jbox2d.common.MathUtils.floor
import org.joml.Vector2f
import org.joml.Vector3f
import util.Settings.GRID_HEIGHT
import util.Settings.GRID_WIDTH
import kotlin.math.max

class GridLines : Component() {

    private val camera = Window.currentScene.camera
    var cameraPos = camera.position
    var projectionSize = camera.getProjectionSize()

    private val defaultColor = Vector3f(0.02f, 0.02f, 0.02f)

    override fun editorUpdate(dt: Float) {
      drawLines()
    }

    private fun drawLines() {
        cameraPos = camera.position
        projectionSize = camera.getProjectionSize()

        val firstX = (floor(cameraPos.x / GRID_WIDTH) - 1) * GRID_HEIGHT
        val firstY = (floor(cameraPos.y / GRID_HEIGHT) - 1) * GRID_HEIGHT

        val numVtLines = (projectionSize.x * camera.zoom / GRID_WIDTH).toInt() + 2
        val numHzLines = (projectionSize.y * camera.zoom  / GRID_HEIGHT).toInt() + 2

        val width = (projectionSize.x * camera.zoom) + GRID_WIDTH * 2
        val height = (projectionSize.y * camera.zoom) + GRID_HEIGHT * 2

        val maxLines = max(numVtLines, numHzLines)

        for (i in 0 until maxLines) {
            val x = firstX + (GRID_WIDTH * i)
            val y = firstY + (GRID_HEIGHT * i)
            if (i < numVtLines) {
                DebugDraw.addLine2D(Vector2f(x, firstY), Vector2f(x, firstY + height), defaultColor, 2)
            }
            if (i < numHzLines) {
                DebugDraw.addLine2D(Vector2f(firstX, y), Vector2f(firstX + width, y), defaultColor, 2)
            }
        }
    }
}