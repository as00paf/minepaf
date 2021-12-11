package marki

import marki.Window.getScene
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.joml.Matrix4f




object MouseListener {
    private var scrollX: Double = 0.0
    private var scrollY: Double = 0.0
    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var lastX: Double = 0.0
    private var lastY: Double = 0.0
    private var isDragging = false
    private var mouseButtonPressed = BooleanArray(9)
    private val gameViewportPos = Vector2f()
    private val gameViewportSize = Vector2f()

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {
        this.lastX = this.xPos
        this.lastY = this.yPos
        this.xPos = xpos
        this.yPos = ypos

        this.isDragging = mouseButtonPressed.any { it }
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            this.mouseButtonPressed[button] = true
        } else if (action == GLFW_RELEASE) {
            this.mouseButtonPressed[button] = false
            this.isDragging = false
        }
    }

    fun mouseScrollCallback(window: Long, offsetX: Double, offsetY: Double) {
        this.scrollX = offsetX
        this.scrollY = offsetY
    }

    fun endFrame() {
        this.scrollX = 0.0
        this.scrollY = 0.0
        this.lastX = this.xPos
        this.lastY = this.yPos
    }

    fun setGameViewportPos(pos: Vector2f) {
        gameViewportPos.set(pos)
    }

    fun setGameViewportSize(size: Vector2f) {
        gameViewportSize.set(size)
    }

    fun getOrthoX(): Float {
        var currentX = getX() - gameViewportPos.x
        currentX = currentX / gameViewportSize.x * 2.0f - 1.0f
        val tmp = Vector4f(currentX, 0f, 0f, 1f)

        val camera = getScene().camera()
        val viewProjection = Matrix4f()
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection)
        tmp.mul(viewProjection)
        currentX = tmp.x

        return currentX
    }

    fun getOrthoY(): Float {
        var currentY = getY() - gameViewportPos.y
        currentY = -(currentY / gameViewportSize.y * 2.0f - 1.0f)
        val tmp = Vector4f(0f, currentY, 0f, 1f)

        val camera = getScene().camera()
        val viewProjection = Matrix4f()
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection)
        tmp.mul(viewProjection)
        currentY = tmp.y

        return currentY
    }

    fun getX(): Float = xPos.toFloat()
    fun getY(): Float = yPos.toFloat()
    fun getDx(): Float = (lastX - xPos).toFloat()
    fun getDy(): Float = (lastY - yPos).toFloat()
    fun getScrollX(): Float = scrollX.toFloat()
    fun getScrollY(): Float = scrollY.toFloat()
    fun isDragging() = isDragging
    fun isMouseButtonDown(button: Int): Boolean {
        return if (button < mouseButtonPressed.size) mouseButtonPressed[button]
        else false
    }
}