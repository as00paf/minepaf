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
    private var worldX: Double = 0.0
    private var worldY: Double = 0.0
    private var lastX: Double = 0.0
    private var lastY: Double = 0.0
    private var lastWorldX: Double = 0.0
    private var lastWorldY: Double = 0.0
    private var isDragging = false
    private var mouseButtonsDown = 0
    private var mouseButtonPressed = BooleanArray(9)
    private val gameViewportPos = Vector2f()
    private val gameViewportSize = Vector2f()

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {
        if(mouseButtonsDown > 0) isDragging = true
        this.lastX = this.xPos
        this.lastY = this.yPos
        this.lastWorldX = worldX
        this.lastWorldY = worldY
        this.xPos = xpos
        this.yPos = ypos
        calcOrthoX()
        calcOrthoY()
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            mouseButtonsDown++
            this.mouseButtonPressed[button] = true
        } else if (action == GLFW_RELEASE) {
            mouseButtonsDown--
            if (button < mouseButtonPressed.size) {
                this.mouseButtonPressed[button] = false
                this.isDragging = false
            }
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
        this.lastWorldX = this.worldX
        this.lastWorldY = this.worldY
    }

    fun setGameViewportPos(pos: Vector2f) {
        gameViewportPos.set(pos)
    }

    fun setGameViewportSize(size: Vector2f) {
        gameViewportSize.set(size)
    }

    fun getOrthoX(): Float {
        return worldX.toFloat()
    }

    fun calcOrthoX() {
        var currentX = getX() - gameViewportPos.x
        currentX = currentX / gameViewportSize.x * 2.0f - 1.0f
        val tmp = Vector4f(currentX, 0f, 0f, 1f)

        val camera = getScene().camera
        val viewProjection = Matrix4f()
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection)
        tmp.mul(viewProjection)

        worldX = tmp.x.toDouble()
    }

    fun getOrthoY(): Float {
        return worldY.toFloat()
    }

    fun calcOrthoY() {
        var currentY = getY() - gameViewportPos.y
        currentY = -(currentY / gameViewportSize.y * 2.0f - 1.0f)
        val tmp = Vector4f(0f, currentY, 0f, 1f)

        val camera = getScene().camera
        val viewProjection = Matrix4f()
        camera.getInverseView().mul(camera.getInverseProjection(), viewProjection)
        tmp.mul(viewProjection)

        worldY = tmp.y.toDouble()
    }

    fun getScreenX(): Float {
        var currentX = getX() - gameViewportPos.x
        currentX = (currentX / gameViewportSize.x) * 1920

        return currentX
    }

    fun getScreenY(): Float {
        var currentY = getY() - gameViewportPos.y
        currentY = 1080 - ((currentY / gameViewportSize.y) * 1080)

        return currentY
    }

    fun getX(): Float = xPos.toFloat()
    fun getY(): Float = yPos.toFloat()
    fun getDx(): Float = (lastX - xPos).toFloat()
    fun getDy(): Float = (lastY - yPos).toFloat()
    fun getWorldDx(): Float = (lastWorldX - worldX).toFloat()
    fun getWorldDy(): Float = (lastWorldY - worldY).toFloat()
    fun getScrollX(): Float = scrollX.toFloat()
    fun getScrollY(): Float = scrollY.toFloat()
    fun isDragging() = isDragging
    fun isMouseButtonDown(button: Int): Boolean {
        return if (button < mouseButtonPressed.size) mouseButtonPressed[button]
        else false
    }
}