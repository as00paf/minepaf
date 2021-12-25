package components

import marki.GameObject
import marki.KeyListener
import marki.MouseListener
import marki.Window
import marki.renderer.DebugDraw
import org.jbox2d.common.MathUtils.floor
import org.joml.Vector2f
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW
import util.Settings

class MouseControls : Component() {
    var holdingObject: GameObject? = null
    private val debounceTime = 0.2f
    private var debounce = debounceTime
    private var boxSelectSet = false
    private val boxSelectStart = Vector2f()
    private val boxSelectEnd = Vector2f()

    fun pickUpObject(go: GameObject) {
        holdingObject?.destroy()
        holdingObject = go
        holdingObject?.getComponent(SpriteRenderer::class.java)?.setColor(0.8f, 0.8f, 0.8f, 0.6f)
        holdingObject?.addComponent(NonPickable())
        Window.currentScene.addGameObjectToScene(go)
    }

    private fun place() {
        val newObj = holdingObject?.copy()
        newObj?.getComponent(StateMachine::class.java)?.refreshTextures()
        newObj?.getComponent(SpriteRenderer::class.java)?.setColor(1f, 1f, 1f, 1f)
        newObj?.removeComponent(NonPickable::class.java)
        Window.currentScene.addGameObjectToScene(newObj!!)
    }

    override fun editorUpdate(dt: Float) {
        debounce -= dt

        val pickingTexture = Window.imGuiLayer.propertiesWindow.pickingTexture
        val scene = Window.currentScene
        val position = holdingObject?.transform?.position
        val x = MouseListener.getWorldX()
        val y = MouseListener.getWorldY()

        println("y: $y")

        if (position != null) {
            position.x = (floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2f
            position.y = (floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2f

            if (MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                place()
                debounce = debounceTime
            }

            if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                holdingObject?.destroy()
                holdingObject = null
            }
        } else if (!MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            val goId = pickingTexture.readPixel(x.toInt(), y.toInt())
            val selectedObject = scene.getGameObject(goId)
            println("selected id = ${goId}")
            val isSelectable = selectedObject?.getComponent(NonPickable::class.java) == null
            if (selectedObject != null && isSelectable) {
                Window.imGuiLayer.propertiesWindow.setActiveObject(selectedObject)
            } else if (selectedObject == null && MouseListener.isDragging().not()) {
                Window.imGuiLayer.propertiesWindow.clearSelected()
            }
            debounce = debounceTime
        } else if (MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            if(!boxSelectSet) {
                Window.imGuiLayer.propertiesWindow.clearSelected()
                boxSelectStart.set(Vector2f(MouseListener.getScreen()))
                boxSelectSet = true
            }

            boxSelectEnd.set(Vector2f(MouseListener.getScreen()))
            val boxSelectStartWorld = Vector2f(MouseListener.screenToWorld(boxSelectStart))
            val boxSelectEndWorld = Vector2f(MouseListener.screenToWorld(boxSelectEnd))
            val halfSize = (Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f)

            DebugDraw.addBox2D(Vector2f(boxSelectStartWorld).add(halfSize), Vector2f(halfSize).mul(2f), 0.0)
        } else if (boxSelectSet) {
            boxSelectSet = false

            var screenStartX  = boxSelectStart.x
            var screenStartY  = boxSelectStart.y
            var screenEndX  = boxSelectEnd.x
            var screenEndY  = boxSelectEnd.y

            boxSelectStart.zero()
            boxSelectEnd.zero()

            // Swap values
            if(screenEndX < screenStartX) {
                val tmp = screenStartX
                screenStartX = screenEndX
                screenEndX = tmp
            }

            if(screenEndY < screenStartY) {
                val tmp = screenStartY
                screenStartY = screenEndY
                screenEndY = tmp
            }

            val gameObjectIds = pickingTexture.readPixels(
                Vector2i(screenStartX.toInt(), screenStartY.toInt()),
                Vector2i(screenEndX.toInt(), screenEndY.toInt())
            ).distinct()

            gameObjectIds.forEach { goId ->
                val selectedObject = Window.currentScene.getGameObject(goId.toInt())
                val isSelectable = selectedObject?.getComponent(NonPickable::class.java) == null
                if(selectedObject != null && isSelectable) {
                    Window.imGuiLayer.propertiesWindow.addActiveGameObject(selectedObject)
                }
            }
        }
    }
}