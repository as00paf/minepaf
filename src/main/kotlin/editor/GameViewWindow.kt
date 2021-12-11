package editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import marki.MouseListener
import marki.Window
import org.joml.Vector2f

object GameViewWindow {

    private var leftX = 0f
    private var rightX = 0f
    private var topY = 0f
    private var bottomY = 0f

    fun imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar or ImGuiWindowFlags.NoScrollWithMouse)

        val windowSize = getLargestSizeForViewport()
        val windowPos = getCenteredPositionForViewport(windowSize)

        ImGui.setCursorPos(windowPos.x, windowPos.y)

        val topLeft = ImVec2()
        ImGui.getCursorScreenPos(topLeft)
        topLeft.x -= ImGui.getScrollX()
        topLeft.y -= ImGui.getScrollY()
        leftX = topLeft.x
        bottomY = topLeft.y
        rightX = topLeft.x + windowSize.x
        topY = topLeft.y + windowSize.y

        MouseListener.setGameViewportPos(Vector2f(topLeft.x, topLeft.y))
        MouseListener.setGameViewportSize(Vector2f(windowSize.x, windowSize.y))

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

    fun getWantCaptureMouse(): Boolean {
        return MouseListener.getX() in leftX..rightX && MouseListener.getY() in bottomY..topY
    }
}