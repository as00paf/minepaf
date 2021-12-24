package editor

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import marki.GameObject
import marki.Window

class SceneHierarchyWindow(private val propertiesWindow:PropertiesWindow) {

    private val payloadDragDropType = "SceneHierarchy"

    fun imgui() {
        ImGui.begin("Scene Hierarchy")

        val gameObjects = Window.currentScene.gameObjects.filter { it.doSerialization() }

        var index = 0
        gameObjects.forEach { obj ->
            val treeNodeOpen = doTreeNode(obj, index)
            if(treeNodeOpen) {
                ImGui.treePop()
            }
            index++
        }

        ImGui.end()
    }

    private fun doTreeNode(obj: GameObject, index: Int):Boolean {
        ImGui.pushID(index)

        val flags = ImGuiTreeNodeFlags.FramePadding or ImGuiTreeNodeFlags.OpenOnArrow or ImGuiTreeNodeFlags.SpanAvailWidth or ImGuiTreeNodeFlags.DefaultOpen
        val result = ImGui.treeNodeEx(obj.name + index.toString(), flags, obj.name)
        if(ImGui.isItemClicked()) propertiesWindow.setActiveObject(obj)
        ImGui.popID()

        if(ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDragDropType, obj)
            ImGui.text(obj.name)
            ImGui.endDragDropSource()
        }

        if(ImGui.beginDragDropTarget()) {
            val payload = ImGui.acceptDragDropPayload<GameObject?>(payloadDragDropType)

            if(payload != null) {
                if(payload.javaClass.isAssignableFrom(GameObject::class.java)) {
                    println("Payload accepted ${payload.name}")
                }
            }

            ImGui.endDragDropTarget()
        }

        return result
    }
}