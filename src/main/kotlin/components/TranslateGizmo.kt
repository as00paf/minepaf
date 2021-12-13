package components

import editor.PropertiesWindow
import marki.GameObject
import marki.Prefabs
import marki.Window
import org.joml.Vector2f
import org.joml.Vector4f

class TranslateGizmo(private val arrowSprite: Sprite, private val propertiesWindow: PropertiesWindow):Component() {

    private val xAxisColor = Vector4f(1f, 0f, 0f, 1f)
    private val xAxisColorHover = Vector4f()
    private val yAxisColor = Vector4f(0f, 0f, 1f, 1f)
    private val yAxisColorHover = Vector4f()

    private val xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16f, 48f, 1)
    private val yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16f, 48f, 1)
    private var xAxisSprite = xAxisObject.getComponent(SpriteRenderer::class.java)
    private var yAxisSprite = yAxisObject.getComponent(SpriteRenderer::class.java)
    private var xAxisOffset = Vector2f(48f, -16f)
    private var yAxisOffset = Vector2f(0f, 48f)

    private var activeGameObject: GameObject? = null

    init {
        Window.getScene().addGameObjectToScene(xAxisObject)
        Window.getScene().addGameObjectToScene(yAxisObject)
    }

    override fun start() {
        xAxisObject.transform.rotation = 90f
        yAxisObject.transform.rotation = 180f
        xAxisObject.setNoSerialize()
        yAxisObject.setNoSerialize()
    }

    override fun update(dt: Float) {
        val go = activeGameObject
        if(go != null) {
            xAxisObject.transform.position.set(go.transform.position)
            yAxisObject.transform.position.set(go.transform.position)

            xAxisObject.transform.position.add(xAxisOffset)
            yAxisObject.transform.position.add(yAxisOffset)

        }

        activeGameObject = propertiesWindow.getActiveGameObject()
        if(activeGameObject != null) setActive() else setInactive()
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
}