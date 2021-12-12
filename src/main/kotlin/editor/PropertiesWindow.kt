package editor

import imgui.ImGui
import marki.GameObject
import marki.MouseListener
import marki.renderer.PickingTexture
import org.lwjgl.glfw.GLFW
import scenes.Scene

class PropertiesWindow(private val pickingTexture: PickingTexture) {
    private var activeGameObject: GameObject? = null

    fun update(dt: Float, currentScene: Scene) {
        if(MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            val x = MouseListener.getScreenX().toInt()
            val y = MouseListener.getScreenY().toInt()

            val goId = pickingTexture.readPixel(x, y)
            activeGameObject = currentScene.getGameObject(goId)

            //println("Pixel: ${pickingTexture.readPixel(x, y)}")
        }
    }

    fun imgui(){
        val go = activeGameObject
        if(go != null) {
            ImGui.begin("Properties: ${go.name}")
            activeGameObject?.imgui()
            ImGui.end()
        }
    }
}