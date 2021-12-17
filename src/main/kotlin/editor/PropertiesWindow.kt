package editor

import components.NonPickable
import imgui.ImGui
import marki.GameObject
import marki.MouseListener
import marki.renderer.PickingTexture
import org.lwjgl.glfw.GLFW
import scenes.Scene

const val DEFAULT_DEBOUNCE = 0.2f

class PropertiesWindow(private val pickingTexture: PickingTexture) {
    private var activeGameObject: GameObject? = null
    private var debounce = DEFAULT_DEBOUNCE

    fun update(dt: Float, currentScene: Scene) {
        debounce -= dt

        if(MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            val x = MouseListener.getScreenX().toInt()
            val y = MouseListener.getScreenY().toInt()

            val goId = pickingTexture.readPixel(x, y)
            val pickedObject = currentScene.getGameObject(goId + 1)
            if(pickedObject != null && pickedObject.getComponent(NonPickable::class.java) == null)
                activeGameObject = pickedObject
            else if(pickedObject == null && MouseListener.isDragging().not()) {
                activeGameObject = null
            }
            debounce = DEFAULT_DEBOUNCE
            //println("Pixel: ${pickingTexture.readPixel(x, y)}")
        }
    }

    fun imgui(){
        val go = activeGameObject
        if(go != null) {
            ImGui.begin("Properties")
            activeGameObject?.imgui()
            ImGui.end()
        }
    }

    fun getActiveGameObject(): GameObject? {
       return activeGameObject
    }
}