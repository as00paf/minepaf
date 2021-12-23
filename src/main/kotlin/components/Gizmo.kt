package components

import editor.PropertiesWindow
import marki.*
import marki.renderer.DebugDraw
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import physics2dtemp.primitives.Collider2D

open class Gizmo(private val arrowSprite: Sprite, private val propertiesWindow: PropertiesWindow):Component() {

    private val xAxisColor = Vector4f(1f, 0.3f, 0.3f, 1f)
    private val xAxisColorHover = Vector4f(1f, 0f, 0f, 1f)
    private val yAxisColor = Vector4f(0.3f, 1f, 0.3f, 1f)
    private val yAxisColorHover = Vector4f(0f, 1f, 0f, 1f)

    private val scale = 100f

    private val gizmoWidth = 16f / scale
    private val gizmoHeight = 48f / scale
    private val xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight, 1)
    private val yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight, 1)
    private var xAxisSprite = xAxisObject.getComponent(SpriteRenderer::class.java)
    private var yAxisSprite = yAxisObject.getComponent(SpriteRenderer::class.java)
    private var xAxisOffset = Vector2f(24f / scale, -6f / scale)
    private var yAxisOffset = Vector2f(-7f / scale, 21f / scale)

    protected var xAxisActive = false
    protected var yAxisActive = false

    protected var activeGameObject: GameObject? = null

    private var inUse = false

    init {
        xAxisObject.addComponent(NonPickable())
        yAxisObject.addComponent(NonPickable())
        Window.currentScene.addGameObjectToScene(xAxisObject)
        Window.currentScene.addGameObjectToScene(yAxisObject)
    }

    override fun start() {
        xAxisObject.transform.rotation = 90.0
        yAxisObject.transform.rotation = 180.0
        xAxisObject.transform.zIndex = 100
        yAxisObject.transform.zIndex = 100
        xAxisObject.setNoSerialize()
        yAxisObject.setNoSerialize()
    }

    override fun update(dt: Float) {
        if(inUse) setInactive()
    }

    override fun editorUpdate(dt: Float) {
        if(!inUse) return
        activeGameObject = propertiesWindow.activeGameObject
        val go = activeGameObject
        if(go != null) {
            setActive()

            // TODO : move this into its own keyEditorBinding component class
            if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW_KEY_D)) {
                val newObject = go.copy()
                Window.currentScene.addGameObjectToScene(newObject)
                newObject.transform.position.add(0.1f, 0.1f)
                propertiesWindow.activeGameObject = newObject
                return
            } else if(KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
                go.destroy()
                setInactive()
                propertiesWindow.activeGameObject = null
            }
        } else {
            setInactive()
            return
        }

        val xAxisHot = checkXHoverState()
        val yAxisHot = checkYHoverState()

        if((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true
            yAxisActive = false
        } else if((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true
            xAxisActive = false
        } else if(!MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && !MouseListener.isDragging()) {
            xAxisActive = false
            yAxisActive = false
        }

        xAxisObject.transform.position.set(go.transform.position)
        yAxisObject.transform.position.set(go.transform.position)

        xAxisObject.transform.position.add(xAxisOffset)
        yAxisObject.transform.position.add(yAxisOffset)
    }

    fun setActive() {
        xAxisSprite = xAxisObject.getComponent(SpriteRenderer::class.java)
        yAxisSprite = yAxisObject.getComponent(SpriteRenderer::class.java)

        xAxisSprite?.setColor(xAxisColor)
        yAxisSprite?.setColor(yAxisColor)
    }

    fun setInactive(){
        activeGameObject = null
        xAxisSprite?.setColor(Vector4f(0f, 0f, 0f, 0f))
        yAxisSprite?.setColor(Vector4f(0f, 0f, 0f, 0f))
    }

    private fun checkXHoverState(): Boolean {
        val mousePos = Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY())
        if(
            mousePos.x in xAxisObject.transform.position.x - (gizmoHeight / 2f)..xAxisObject.transform.position.x + (gizmoHeight / 2f) &&
            mousePos.y in xAxisObject.transform.position.y..xAxisObject.transform.position.y + (gizmoWidth)
        ) {
            xAxisSprite?.setColor(xAxisColorHover)
            return true
        }

        xAxisSprite?.setColor(xAxisColor)
        return false
    }

    private fun checkYHoverState(): Boolean {
        val mousePos = Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY())
        if(
            mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2f) &&
            mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2f) &&
            mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2f) &&
            mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2f)) {

            yAxisSprite?.setColor(yAxisColorHover)
            return true
        }

        yAxisSprite?.setColor(yAxisColor)
        return false
    }

    fun isInUse():Boolean = inUse
    fun setNotInUse() {
        inUse = false
        setInactive()
    }
    fun setInUse() {
        inUse = true
    }
}