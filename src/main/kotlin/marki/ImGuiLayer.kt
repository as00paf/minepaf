package marki

import editor.GameViewWindow
import editor.MenuBar
import editor.PropertiesWindow
import editor.SceneHierarchyWindow
import imgui.ImFontConfig
import imgui.ImGui
import imgui.ImGuiIO
import imgui.callback.ImStrConsumer
import imgui.callback.ImStrSupplier
import imgui.flag.ImGuiCond
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.type.ImBoolean
import marki.renderer.PickingTexture
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import scenes.Scene


const val glslVersion = "#version 130"

class ImGuiLayer(val pickingTexture: PickingTexture) {

    val propertiesWindow = PropertiesWindow(pickingTexture)
    val gameViewWindow = GameViewWindow()
    val hierarchyWindow = SceneHierarchyWindow(propertiesWindow)

    private val imGuiGlfw = ImGuiImplGlfw()
    private val imGuiGl3 = ImGuiImplGl3()
    private val menuBar = MenuBar()

    fun init(glfwWindow: Long) {
        ImGui.createContext()

        // IO
        val io = ImGui.getIO()
        io.iniFilename = "imgui.ini"
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable or ImGuiConfigFlags.ViewportsEnable)
        io.backendPlatformName = "imgui_java_impl_glfw"

        setCallbacks(glfwWindow, io)
        setupFonts(io)

        imGuiGlfw.init(glfwWindow, true)
        imGuiGl3.init(glslVersion)
    }

    private fun setCallbacks(glfwWindow: Long, io: ImGuiIO) {
        glfwSetKeyCallback(glfwWindow) { w: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true)
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false)
            }
            io.keyCtrl = io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL)
            io.keyShift = io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT)
            io.keyAlt = io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT)
            io.keySuper = io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER)

            if (!io.wantCaptureKeyboard) {
                KeyListener.keyCallback(w, key, scancode, action, mods)
            }
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback)

        glfwSetMouseButtonCallback(glfwWindow) { w: Long, button: Int, action: Int, mods: Int ->
            val mouseDown = BooleanArray(5)
            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE
            io.setMouseDown(mouseDown)
            if (!io.wantCaptureMouse && mouseDown[1]) {
                ImGui.setWindowFocus(null)
            }

            if (!io.wantCaptureMouse || gameViewWindow.getWantCaptureMouse()) {
                MouseListener.mouseButtonCallback(w, button, action, mods)
            }
        }

        glfwSetScrollCallback(glfwWindow) { w: Long, xOffset: Double, yOffset: Double ->
            io.mouseWheelH = io.mouseWheelH + xOffset.toFloat()
            io.mouseWheel = io.mouseWheel + yOffset.toFloat()

            if (!io.wantCaptureMouse || gameViewWindow.getWantCaptureMouse()) {
                MouseListener.mouseScrollCallback(w, xOffset, yOffset)
            } else {
                MouseListener.clear()
            }
        }

        glfwSetWindowSizeCallback(glfwWindow) { w: Long, newWidth: Int, newHeight: Int ->
            Window.setWidth(newWidth)
            Window.setHeight(newHeight)
        }

        io.setSetClipboardTextFn(object : ImStrConsumer() {
            override fun accept(s: String) {
                glfwSetClipboardString(glfwWindow, s)
            }
        })

        io.setGetClipboardTextFn(object : ImStrSupplier() {
            override fun get(): String {
                val clipboardString = glfwGetClipboardString(glfwWindow)
                return clipboardString ?: ""
            }
        })
    }

    private fun setupFonts(io: ImGuiIO) {
        val fontAtlas = io.fonts
        val fontConfig = ImFontConfig()

        fontConfig.glyphRanges = fontAtlas.glyphRangesDefault
        fontConfig.pixelSnapH = true
        fontAtlas.addFontFromFileTTF("assets/fonts/Roboto-Medium.ttf", 24f, fontConfig)

        fontConfig.destroy()
    }

    fun imGui(dt: Float, currentScene: Scene) {
        startFrame()

        setupDockSpace()
        currentScene.imgui()
        gameViewWindow.imgui()
        propertiesWindow.imgui()
        hierarchyWindow.imgui()

        endFrame()
    }

    private fun startFrame() {
        imGuiGlfw.newFrame()
        ImGui.newFrame()
    }

    private fun endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glViewport(0, 0, Window.getWidth(), Window.getHeight())
        glClearColor(0f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)

        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupWindowPtr = glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            glfwMakeContextCurrent(backupWindowPtr)
        }
    }

    private fun setupDockSpace() {
        val windowFlags =
            ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoDocking or ImGuiWindowFlags.NoBringToFrontOnFocus or
            ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoNavFocus

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val mainViewport = ImGui.getMainViewport()
            ImGui.setNextWindowPos(mainViewport.workPosX, mainViewport.workPosY)
            ImGui.setNextWindowSize(mainViewport.workSizeX, mainViewport.workSizeY)
            ImGui.setNextWindowViewport(mainViewport.id)
        }

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always)
        ImGui.setNextWindowSize(Window.getWidth().toFloat(), Window.getHeight().toFloat())
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 1.0f)

        ImGui.begin("Dockspace Demo", ImBoolean(true), windowFlags)
        ImGui.popStyleVar(2)

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"))
        menuBar.imgui()
        ImGui.end()
    }

    fun destroy() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
    }
}