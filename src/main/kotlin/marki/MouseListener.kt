package marki

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE


object MouseListener {
    private var scrollX: Double = 0.0
    private var scrollY: Double = 0.0
    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var lastX: Double = 0.0
    private var lastY: Double = 0.0
    private var worldX: Double = 0.0
    private var worldY: Double = 0.0
    private var lastWorldX: Double = 0.0
    private var lastWorldY: Double = 0.0
    private var isDragging = false
    private var mouseButtonsDown = 0
    private var mouseButtonPressed = BooleanArray(9)
    private val gameViewportPos = Vector2f()
    private val gameViewportSize = Vector2f()

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {
        if (mouseButtonsDown > 0) isDragging = true
        lastX = xPos
        lastY = yPos
        lastWorldX = worldX
        lastWorldY = worldY
        this.xPos = xpos
        this.yPos = ypos
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
        this.lastWorldX = this.worldX
        this.lastWorldY = this.worldY
    }

    fun getWorldDx(): Float {
        return (lastWorldX - worldX).toFloat()
    }

    fun getWorldDy(): Float {
        return (lastWorldY - worldY).toFloat()
    }

    fun setGameViewportPos(pos: Vector2f) {
        gameViewportPos.set(pos)
    }

    fun setGameViewportSize(size: Vector2f) {
        gameViewportSize.set(size)
    }

    fun getWorldX():Float {
        return getWorld().x
    }

    fun getWorldY():Float {
        return getWorld().y
    }

    fun getWorld():Vector2f {
        var currentX = getX() - gameViewportPos.x
        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f

        var currentY = getY() - gameViewportPos.y
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f)
        val tmp = Vector4f(currentX, currentY, 0f, 1f)

        val camera = Window.currentScene.camera
        val inverseView = Matrix4f(camera.getInverseView())
        val inverseProjection = Matrix4f(camera.getInverseProjection())
        tmp.mul(inverseView.mul(inverseProjection))

        worldX = tmp.x.toDouble()
        worldY = tmp.y.toDouble()

        return Vector2f(tmp.x, tmp.y)
    }

    fun getScreenX(): Float {
        return getScreen().x
    }

    fun getScreenY(): Float {
       return getScreen().y
    }

    fun getScreen():Vector2f {
        var currentX = getX() - gameViewportPos.x
        currentX = (currentX / gameViewportSize.x) * 1920

        var currentY = getY() - gameViewportPos.y
        currentY = 1080 - ((currentY / gameViewportSize.y) * 1080)

        return Vector2f(currentX, currentY)
    }

    fun getX(): Float = xPos.toFloat()
    fun getY(): Float = yPos.toFloat()
    fun getScrollX(): Float = scrollX.toFloat()
    fun getScrollY(): Float = scrollY.toFloat()
    fun isDragging() = isDragging
    fun isMouseButtonDown(button: Int): Boolean {
        return if (button < mouseButtonPressed.size) mouseButtonPressed[button]
        else false
    }
}