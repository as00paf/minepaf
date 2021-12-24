package marki

import marki.Window.getHeight
import marki.Window.getWidth
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
        if (!Window.imGuiLayer.gameViewWindow.getWantCaptureMouse()) clear()
        if (mouseButtonsDown > 0) isDragging = true
        this.lastX = this.xPos
        this.lastY = this.yPos
        this.lastWorldX = this.worldX
        this.lastWorldY = this.worldY
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
    }

    fun clear() {
        this.scrollX = 0.0
        this.scrollY = 0.0
        this.xPos = 0.0
        this.yPos = 0.0
        this.lastX = 0.0
        this.lastY = 0.0
        this.mouseButtonsDown = 0
        this.isDragging = false
        this.mouseButtonPressed.fill(false)

    }

    fun getWorldDx(): Float {
        return (this.lastWorldX - this.worldX).toFloat()
    }

    fun getWorldDy(): Float {
        return (this.lastWorldY - this.worldY).toFloat()
    }

    fun setGameViewportPos(pos: Vector2f) {
        this.gameViewportPos.set(pos)
    }

    fun setGameViewportSize(size: Vector2f) {
        this.gameViewportSize.set(size)
    }

    fun getWorldX():Float {
        return getWorld().x
    }

    fun getWorldY():Float {
        return getWorld().y
    }

    fun getWorld(): Vector2f {
        var currentX: Float = getX() - gameViewportPos.x
        currentX = (2.0f * (currentX / gameViewportSize.x)) - 1.0f
        var currentY: Float = getY() - gameViewportPos.y
        currentY = (2.0f * (1.0f - (currentY / gameViewportSize.y))) - 1

        val camera = Window.currentScene.camera
        val tmp = Vector4f(currentX, currentY, 0f, 1f)
        val inverseView = Matrix4f(camera.getInverseView())
        val inverseProjection = Matrix4f(camera.getInverseProjection())
        tmp.set(tmp.mul(inverseView.mul(inverseProjection)))

        return Vector2f(tmp.x, tmp.y)
    }

    fun screenToWorld(screenCoords:Vector2f):Vector2f {
        val normalizedScreenCoords = Vector2f(screenCoords.x / getWidth(), screenCoords.y / getHeight())
        normalizedScreenCoords.mul(2.0f).sub(Vector2f(1.0f, 1.0f))
        val camera: Camera = Window.currentScene.camera
        val tmp = Vector4f(normalizedScreenCoords.x, normalizedScreenCoords.y, 0f, 1f)
        val inverseView = Matrix4f(camera.getInverseView())
        val inverseProjection = Matrix4f(camera.getInverseProjection())
        tmp.mul(inverseView.mul(inverseProjection))
        return Vector2f(tmp.x, tmp.y)
    }

    fun worldToScreen(worldCoords:Vector2f):Vector2f {
        val camera: Camera = Window.currentScene.camera
        val ndcSpacePos = Vector4f(worldCoords.x, worldCoords.y, 1.0f, 1f)
        val view = Matrix4f(camera.getViewMatrix())
        val projection = Matrix4f(camera.getProjectionMatrix())
        ndcSpacePos.mul(projection.mul(view))
        val windowSpace = Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w)
        windowSpace.add(Vector2f(1.0f, 1.0f)).mul(0.5f)
        windowSpace.mul(Vector2f(getWidth().toFloat(), getHeight().toFloat()))

        return windowSpace
    }

    fun getScreenX(): Float {
        return getScreen().x
    }

    fun getScreenY(): Float {
       return getScreen().y
    }

    fun getScreen():Vector2f {
        var currentX: Float = getX() - gameViewportPos.x
        currentX = (currentX / gameViewportSize.x) * 1920
        var currentY: Float = getY() - gameViewportPos.y
        currentY = (1.0f - (currentY / gameViewportSize.y)) * 1080
        return Vector2f(currentX, currentY)
    }

    fun getX(): Float = this.xPos.toFloat()
    fun getY(): Float = this.yPos.toFloat()
    fun getScrollX(): Float = this.scrollX.toFloat()
    fun getScrollY(): Float = this.scrollY.toFloat()
    fun isDragging() = this.isDragging
    fun isMouseButtonDown(button: Int): Boolean {
        return if (button < this.mouseButtonPressed.size) this.mouseButtonPressed[button]
        else false
    }
}