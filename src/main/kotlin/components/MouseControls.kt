package components

import marki.GameObject
import marki.KeyListener
import marki.MouseListener
import marki.Window
import org.jbox2d.common.MathUtils.floor
import org.lwjgl.glfw.GLFW
import util.Settings

class MouseControls: Component() {
    var holdingObject: GameObject? = null
    private val debounceTime = 0.2f
    private var debounce = debounceTime

    fun pickUpObject(go: GameObject) {
        holdingObject?.destroy()
        holdingObject = go
        holdingObject?.getComponent(SpriteRenderer::class.java)?.setColor(0.8f, 0.8f, 0.8f, 0.6f)
        holdingObject?.addComponent(NonPickable())
        Window.currentScene.addGameObjectToScene(go)
    }

    fun place(){
        val newObj = holdingObject?.copy()
        newObj?.getComponent(StateMachine::class.java)?.refreshTextures()
        newObj?.getComponent(SpriteRenderer::class.java)?.setColor(1f, 1f, 1f, 1f)
        newObj?.removeComponent(NonPickable::class.java)
        Window.currentScene.addGameObjectToScene(newObj!!)
    }

    override fun editorUpdate(dt: Float) {
        debounce -= dt
        if(debounce <= 0) {
            holdingObject?.transform?.position?.let {
                it.x = MouseListener.getWorldX()
                it.y = MouseListener.getWorldY()
                it.x = (floor(it.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2
                it.y = (floor(it.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2

                if(MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    place()
                    debounce = debounceTime
                }

                if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                    holdingObject?.destroy()
                    holdingObject = null
                }
            }
        }
    }
}