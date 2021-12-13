package components

import marki.Camera
import marki.KeyListener
import marki.MouseListener
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_KEY_HOME
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.sign
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE




class EditorCamera(private val levelEditorCamera: Camera):Component() {

    private val clickOrigin = Vector2f()
    private var dragDebounce = 0.032f
    private var dragSensitivity = 30f
    private var scrollSensitivity = 0.1f
    private var lerpTime = 0.0f
    private var reset = false

    override fun update(dt: Float) {
        drag(dt)
        zoom()
        resetIfNeeded(dt)
    }

    private fun drag(dt: Float) {
        if(MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0f) {
            clickOrigin.set(Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY()))
            dragDebounce -= dt
            return
        } else if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            val mousePos = Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY())
            val mouseDelta = Vector2f(mousePos).sub(clickOrigin)
            levelEditorCamera.position.sub(mouseDelta.mul(dt).mul(dragSensitivity))
            clickOrigin.lerp(mousePos, dt)
        } else if(dragDebounce <= 0f ) {
            dragDebounce =  0.032f
        }
    }

    private fun zoom() {
        var addValue = abs(MouseListener.getScrollY() * scrollSensitivity).toDouble().pow(1.0 / levelEditorCamera.zoom)
        addValue *= -sign(MouseListener.getScrollY())
        levelEditorCamera.addZoom(addValue.toFloat())
    }

    private fun resetIfNeeded(dt: Float) {
        if(KeyListener.isKeyPressed(GLFW_KEY_HOME)) {
            reset = true
        }

        if(reset) {
            levelEditorCamera.position.lerp(Vector2f(), lerpTime)
            levelEditorCamera.zoom += ((1.0f - levelEditorCamera.zoom) * lerpTime)
            lerpTime += 0.1f * dt
            if(abs(levelEditorCamera.position.x) <= 5.0f && abs(levelEditorCamera.position.y) <= 5.0f) {
                levelEditorCamera.position.set(-0f, 0f)
                reset = false
                levelEditorCamera.zoom = 1f
                lerpTime = 0f
            }
        }
    }
}