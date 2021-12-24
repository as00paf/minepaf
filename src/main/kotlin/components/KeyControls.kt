package components

import marki.GameObject
import marki.KeyListener
import marki.Window
import org.lwjgl.glfw.GLFW
import util.Settings

class KeyControls:Component() {

    override fun editorUpdate(dt: Float) {
        val propertiesWindow = Window.imGuiLayer.propertiesWindow
        val activeGameObject = propertiesWindow.getActiveObject()
        val activeGameObjects = propertiesWindow.activeGameObjects


        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW.GLFW_KEY_D) && activeGameObject != null) {
            val newObject = activeGameObject.copy()
            Window.currentScene.addGameObjectToScene(newObject)
            newObject.transform.position.add(Settings.GRID_WIDTH, 0.0f)
            propertiesWindow.setActiveObject(newObject)
        } else if (KeyListener.keyBeginPress(GLFW.GLFW_KEY_LEFT_CONTROL) && KeyListener.keyBeginPress(GLFW.GLFW_KEY_D) && activeGameObjects.size > 1) {
            val gos = ArrayList<GameObject>()
            gos.addAll(activeGameObjects)
            propertiesWindow.clearSelected()
            gos.forEach { go ->
                val copy = go.copy()
                Window.currentScene.addGameObjectToScene(copy)
                propertiesWindow.addActiveGameObject(copy)
            }
        } else if (KeyListener.keyBeginPress(GLFW.GLFW_KEY_DELETE) && activeGameObjects.size > 0) {
            activeGameObjects.forEach { it.destroy() }
            propertiesWindow.clearSelected()
        }
    }
}