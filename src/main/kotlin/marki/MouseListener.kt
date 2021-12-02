package marki

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*

object MouseListener {
    fun get(): MouseListener = this

    private var scrollX: Double = 0.0
    private var scrollY: Double = 0.0
    private var xPos: Double = 0.0
    private var yPos: Double = 0.0
    private var lastX: Double = 0.0
    private var lastY: Double = 0.0
    private var isDragging = false
    private var mouseButtonPressed = BooleanArray(3)

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {
        this.lastX = this.xPos
        this.lastY = this.yPos
        this.xPos = xpos
        this.yPos = ypos

        this.isDragging = mouseButtonPressed.any { it }
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if(action == GLFW_PRESS){
            this.mouseButtonPressed[button] = true
        }else if(action == GLFW_RELEASE) {
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

    fun getX():Float = xPos.toFloat()
    fun getY():Float = yPos.toFloat()
    fun getDx():Float = (lastX - xPos).toFloat()
    fun getDy():Float = (lastY - yPos).toFloat()
    fun getScrollX(): Float = scrollX.toFloat()
    fun getScrollY(): Float = scrollY.toFloat()
    fun isDragging() = isDragging
    fun isMouseButtonDown(button: Int): Boolean {
        return if(button < mouseButtonPressed.size) mouseButtonPressed[button]
        else false
    }
}