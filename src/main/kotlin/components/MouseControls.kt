package components

import marki.GameObject
import marki.MouseListener
import marki.Window
import org.lwjgl.glfw.GLFW
import util.Settings

class MouseControls: Component() {
    var holdingObject: GameObject? = null

    fun pickUpObject(go: GameObject) {
        holdingObject = go
        Window.getScene().addGameObjectToScene(go)
    }

    fun place(){
        holdingObject = null
    }

    override fun update(dt: Float) {
        holdingObject?.let {
            it.transform.position.x = (MouseListener.getOrthoX() / Settings.GRID_WIDTH).toInt() * Settings.GRID_WIDTH.toFloat()
            it.transform.position.y = (MouseListener.getOrthoY() / Settings.GRID_HEIGHT).toInt() * Settings.GRID_HEIGHT.toFloat()

            if(MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                place()
            }
        }
    }
}