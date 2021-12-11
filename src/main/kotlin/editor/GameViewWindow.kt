package editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import marki.Window

object GameViewWindow {

    fun imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar or ImGuiWindowFlags.NoScrollWithMouse)

        val windowSize = getLargestSizeForViewport()
        val windowPos = getCenteredPositionForViewport(windowSize)

        ImGui.setCursorPos(windowPos.x, windowPos.y)
        val texId = Window.frameBuffer.getTextureId()
        ImGui.image(texId, windowSize.x, windowSize.y, 0f, 1f, 1f, 0f)

        ImGui.end()
    }

    private fun getLargestSizeForViewport(): ImVec2 {
        val windowSize = ImVec2()
        ImGui.getContentRegionAvail(windowSize)
        windowSize.x -= ImGui.getScrollX()
        windowSize.y -= ImGui.getScrollY()

        var aspectWidth = windowSize.x
        var aspectHeight = aspectWidth / Window.getTargetAspectRatio()
        if(aspectHeight > windowSize.y) {
            // Switch to pillarbox mode
            aspectHeight = windowSize.y
            aspectWidth = aspectHeight * Window.getTargetAspectRatio()
        }

        return ImVec2(aspectWidth, aspectHeight)
    }

    private fun getCenteredPositionForViewport(aspectSize: ImVec2): ImVec2 {
        val windowSize = ImVec2()
        ImGui.getContentRegionAvail(windowSize)
        windowSize.x -= ImGui.getScrollX()
        windowSize.y -= ImGui.getScrollY()

        val viewportX = (windowSize.x / 2f) - (aspectSize.x / 2f)
        val viewportY = (windowSize.y / 2f) - (aspectSize.y / 2f)

        return ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY())
    }
}