package components

import editor.PropertiesWindow
import marki.GameObject
import marki.MouseListener
import marki.Prefabs
import marki.Window
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT

open class Gizmo(private val arrowSprite: Sprite, private val propertiesWindow: PropertiesWindow):Component() {

    private val xAxisColor = Vector4f(1f, 0.3f, 0.3f, 1f)
    private val xAxisColorHover = Vector4f(1f, 0f, 0f, 1f)
    private val yAxisColor = Vector4f(0.3f, 1f, 0.3f, 1f)
    private val yAxisColorHover = Vector4f(0f, 1f, 0f, 1f)

    private val xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16f, 48f, 1)
    private val yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16f, 48f, 1)
    private var xAxisSprite = xAxisObject.getComponent(SpriteRenderer::class.java)
    private var yAxisSprite = yAxisObject.getComponent(SpriteRenderer::class.java)
    private var xAxisOffset = Vector2f(48f, -16f)
    private var yAxisOffset = Vector2f(0f, 48f)
    private val gizmoWidth = 16
    private val gizmoHeight = 48

    protected var xAxisActive = false
    protected var yAxisActive = false

    protected var activeGameObject: GameObject? = null

    private var inUse = false

    init {
        xAxisObject.addComponent(NonPickable())
        yAxisObject.addComponent(NonPickable())
        Window.getScene().addGameObjectToScene(xAxisObject)
        Window.getScene().addGameObjectToScene(yAxisObject)
    }

    override fun start() {
        xAxisObject.transform.rotation = 90f
        yAxisObject.transform.rotation = 180f
        xAxisObject.transform.zIndex = 100
        yAxisObject.transform.zIndex = 100
        xAxisObject.setNoSerialize()
        yAxisObject.setNoSerialize()
    }

    override fun update(dt: Float) {
        if(!inUse) return
        activeGameObject = propertiesWindow.getActiveGameObject()
        val go = activeGameObject
        if(go != null) setActive() else {
            setInactive()
            return
        }

        val xAxisHot = checkXHoverState()
        val yAxisHot = checkYHoverState()

        if((xAxisHot or xAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true
            yAxisActive = false
        } else if((yAxisHot or yAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true
            xAxisActive = false
        } else {
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

    fun checkXHoverState(): Boolean {
        val mousePos = Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY())
        if(
            mousePos.x <= xAxisObject.transform.position.x &&
            mousePos.x >= xAxisObject.transform.position.x - gizmoHeight &&
            mousePos.y >= xAxisObject.transform.position.y &&
            mousePos.y <= xAxisObject.transform.position.y + gizmoWidth) {

            xAxisSprite?.setColor(xAxisColorHover)
            return true
        }

        xAxisSprite?.setColor(xAxisColor)
        return false
    }

    fun checkYHoverState(): Boolean {
        val mousePos = Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY())
        if(
            mousePos.x <= yAxisObject.transform.position.x &&
            mousePos.x >= yAxisObject.transform.position.x - gizmoWidth &&
            mousePos.y <= yAxisObject.transform.position.y &&
            mousePos.y >= yAxisObject.transform.position.y - gizmoHeight) {

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