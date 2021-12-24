package editor

import imgui.ImGui
import marki.GameObject
import marki.renderer.PickingTexture
import physics2d.components.Box2DCollider
import physics2d.components.CircleCollider
import physics2d.components.RigidBody2D

class PropertiesWindow(val pickingTexture: PickingTexture) {
    val activeGameObjects = mutableListOf<GameObject>()
    private var activeGameObject: GameObject? = null

    fun imgui() {
        val go = if (activeGameObjects.size > 0) activeGameObjects[0] else null
        if (activeGameObjects.size == 1 && go != null) {
            activeGameObject = activeGameObjects[0]
            ImGui.begin("Properties: ${go.name}")

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (go.getComponent(RigidBody2D::class.java) == null) {
                        go.addComponent(RigidBody2D())
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (go.getComponent(Box2DCollider::class.java) == null && go.getComponent(CircleCollider::class.java) == null) {
                        go.addComponent(Box2DCollider())
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (go.getComponent(CircleCollider::class.java) == null && go.getComponent(Box2DCollider::class.java) == null) {
                        go.addComponent(CircleCollider())
                    }
                }

                ImGui.endPopup()
            }

            activeGameObject?.imgui()
            ImGui.end()
        }
    }

    fun clearSelected() {
        activeGameObjects.clear()
    }

    fun addActiveGameObject(go: GameObject) {
        activeGameObjects.add(go)
    }

    fun getActiveObject() = if (activeGameObjects.size == 1) activeGameObjects[0] else null
    fun setActiveObject(go: GameObject) {
        go?.let {
            clearSelected()
            activeGameObjects.add(go)
        }
    }
}