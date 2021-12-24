package editor

import components.NonPickable
import imgui.ImGui
import marki.GameObject
import marki.MouseListener
import marki.renderer.PickingTexture
import org.lwjgl.glfw.GLFW
import physics2d.components.Box2DCollider
import physics2d.components.CircleCollider
import physics2d.components.RigidBody2D
import scenes.Scene

const val DEFAULT_DEBOUNCE = 0.2f

class PropertiesWindow(private val pickingTexture: PickingTexture) {
    var activeGameObject: GameObject? = null

    private var debounce = DEFAULT_DEBOUNCE

    fun update(dt: Float, currentScene: Scene) {
        debounce -= dt

        if(!MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            val x = MouseListener.getScreenX().toInt()
            val y = MouseListener.getScreenY().toInt()

            val goId = pickingTexture.readPixel(x, y)
            val selectedObject = currentScene.getGameObject(goId)
            val isSelectable = selectedObject?.getComponent(NonPickable::class.java) == null
            if(selectedObject != null && isSelectable)
                activeGameObject = selectedObject
            else if(selectedObject == null && MouseListener.isDragging().not()) {
                activeGameObject = null
            }
            debounce = DEFAULT_DEBOUNCE
        }
    }

    fun imgui(){
        val go = activeGameObject
        if(go != null) {
            ImGui.begin("Properties: ${go.name}")

            if(ImGui.beginPopupContextWindow("ComponentAdder")) {
                if(ImGui.menuItem("Add Rigidbody")) {
                    if(go.getComponent(RigidBody2D::class.java) == null) {
                        go.addComponent(RigidBody2D())
                    }
                }

                if(ImGui.menuItem("Add Box Collider")) {
                    if(go.getComponent(Box2DCollider::class.java) == null && go.getComponent(CircleCollider::class.java) == null) {
                        go.addComponent(Box2DCollider())
                    }
                }

                if(ImGui.menuItem("Add Circle Collider")) {
                    if(go.getComponent(CircleCollider::class.java) == null && go.getComponent(Box2DCollider::class.java) == null) {
                        go.addComponent(CircleCollider())
                    }
                }

                ImGui.endPopup()
            }

            activeGameObject?.imgui()
            ImGui.end()
        }
    }
}