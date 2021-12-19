package editor

import imgui.ImGui
import observers.EventSystem
import observers.events.EventType

class MenuBar {

    fun imgui() {
        ImGui.beginMainMenuBar()

        if(ImGui.beginMenu("File")){
            if(ImGui.menuItem("Save", "Ctrl+S")){
                EventSystem.notify(EventType.SaveLevel)
            }
            if(ImGui.menuItem("Load", "Ctrl+O")){
                EventSystem.notify(EventType.LoadLevel)
            }
            ImGui.endMenu()
        }

        ImGui.endMainMenuBar()
    }
}