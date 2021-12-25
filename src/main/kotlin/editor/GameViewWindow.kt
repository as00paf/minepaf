package editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import marki.MouseListener
import marki.Window
import observers.EventSystem
import observers.events.Event
import observers.events.EventType
import org.joml.Vector2f
import java.lang.Math.round
import kotlin.math.roundToInt

class GameViewWindow {

    private var leftX = 0f
    private var rightX = 0f
    private var topY = 0f
    private var bottomY = 0f
    private var isPlaying = false
    private var created = false

    fun imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar or ImGuiWindowFlags.NoScrollWithMouse or ImGuiWindowFlags.MenuBar)

        ImGui.beginMenuBar()

        if(ImGui.menuItem("Play", "", isPlaying, !isPlaying)){
            isPlaying = true
            EventSystem.notify(EventType.GameEngineStartPlay)
        }

        if(ImGui.menuItem("Stop", "", !isPlaying, isPlaying)){
            isPlaying = false
            EventSystem.notify(EventType.GameEngineStopPlay)
        }

        if(created) {
            ImGui.text("Mouse Screen (${MouseListener.getScreenX().roundToInt()},${MouseListener.getScreenY().roundToInt()})")
            ImGui.sameLine()
            ImGui.text("World Screen (${MouseListener.getWorldX().roundToInt()},${MouseListener.getWorldY().roundToInt()})")
            ImGui.sameLine()
            ImGui.text("ImGui (${ImGui.getCursorPosX().roundToInt()},${ImGui.getCursorPosY().roundToInt()})")
        }

        ImGui.endMenuBar()

        ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY())
        val windowSize = getLargestSizeForViewport()
        val windowPos = getCenteredPositionForViewport(windowSize)
        ImGui.setCursorPos(windowPos.x, windowPos.y) // this line causes problems

        val offset = 10
        leftX = windowPos.x + offset
        rightX = windowPos.x + windowSize.x + offset
        bottomY = windowPos.y
        topY = windowPos.y + windowSize.y

        val texId = Window.frameBuffer.getTextureId()
        ImGui.image(texId, windowSize.x, windowSize.y, 0f, 1f, 1f, 0f)

        MouseListener.setGameViewportPos(Vector2f(windowPos.x + offset, windowPos.y))
        MouseListener.setGameViewportSize(Vector2f(windowSize.x, windowSize.y))

        ImGui.end()
        if(!created) created = true
    }

    private fun getLargestSizeForViewport(): ImVec2 {
        val windowSize = ImVec2()
        ImGui.getContentRegionAvail(windowSize)

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

        val viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f)
        val viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f)

        return ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY())
    }

    fun getWantCaptureMouse(): Boolean {
        return MouseListener.getX() in leftX..rightX && MouseListener.getY() in bottomY..topY
    }
}