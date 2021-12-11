package marki

import editor.GameViewWindow
import imgui.ImFontConfig
import imgui.ImGui
import imgui.callback.ImStrConsumer
import imgui.callback.ImStrSupplier
import imgui.flag.*
import org.lwjgl.glfw.GLFW
import scenes.Scene
import imgui.type.ImBoolean

import imgui.flag.ImGuiWindowFlags

import imgui.flag.ImGuiStyleVar

import imgui.flag.ImGuiCond


class ImGuiLayer() {

    fun init(glfwWindow: Long) {
        ImGui.createContext()

        // IO
        val io = ImGui.getIO()
        io.iniFilename = "imgui.ini"
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard)
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable)
        io.backendFlags = ImGuiBackendFlags.HasMouseCursors
        io.backendPlatformName = "imgui_java_impl_glfw"

        // Keyboard mapping
        val keyMap = IntArray(ImGuiKey.COUNT)
        keyMap[ImGuiKey.Tab] = GLFW.GLFW_KEY_TAB
        keyMap[ImGuiKey.LeftArrow] = GLFW.GLFW_KEY_LEFT
        keyMap[ImGuiKey.RightArrow] = GLFW.GLFW_KEY_RIGHT
        keyMap[ImGuiKey.UpArrow] = GLFW.GLFW_KEY_UP
        keyMap[ImGuiKey.DownArrow] = GLFW.GLFW_KEY_DOWN
        keyMap[ImGuiKey.PageUp] = GLFW.GLFW_KEY_PAGE_UP
        keyMap[ImGuiKey.PageDown] = GLFW.GLFW_KEY_PAGE_DOWN
        keyMap[ImGuiKey.Home] = GLFW.GLFW_KEY_HOME
        keyMap[ImGuiKey.End] = GLFW.GLFW_KEY_END
        keyMap[ImGuiKey.Insert] = GLFW.GLFW_KEY_INSERT
        keyMap[ImGuiKey.Delete] = GLFW.GLFW_KEY_DELETE
        keyMap[ImGuiKey.Backspace] = GLFW.GLFW_KEY_BACKSPACE
        keyMap[ImGuiKey.Space] = GLFW.GLFW_KEY_SPACE
        keyMap[ImGuiKey.Enter] = GLFW.GLFW_KEY_ENTER
        keyMap[ImGuiKey.Escape] = GLFW.GLFW_KEY_ESCAPE
        keyMap[ImGuiKey.KeyPadEnter] = GLFW.GLFW_KEY_KP_ENTER
        keyMap[ImGuiKey.A] = GLFW.GLFW_KEY_A
        keyMap[ImGuiKey.C] = GLFW.GLFW_KEY_C
        keyMap[ImGuiKey.V] = GLFW.GLFW_KEY_V
        keyMap[ImGuiKey.X] = GLFW.GLFW_KEY_X
        keyMap[ImGuiKey.Y] = GLFW.GLFW_KEY_Y
        keyMap[ImGuiKey.Z] = GLFW.GLFW_KEY_Z
        io.setKeyMap(keyMap)

        // Mouse cursors mapping
        Window.mouseCursors[ImGuiMouseCursor.Arrow] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.TextInput] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.ResizeAll] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.ResizeNS] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.ResizeEW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.ResizeNESW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.ResizeNWSE] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.Hand] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR)
        Window.mouseCursors[ImGuiMouseCursor.NotAllowed] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR)

        // GLFW callbacks to handle user input
        /* glfwSetKeyCallback(glfwWindow) { w: Long, key: Int, scancode: Int, action: Int, mods: Int ->
             if (action == GLFW_PRESS) {
                 io.setKeysDown(key, true)
             } else if (action == GLFW_RELEASE) {
                 io.setKeysDown(key, false)
             }
             io.keyCtrl = io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL)
             io.keyShift = io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT)
             io.keyAlt = io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT)
             io.keySuper = io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER)

             if(!io.wantCaptureKeyboard) {
                 //KeyListener.keyCallback(w, key, scancode, action, mods)
             }
         }

        glfwSetCharCallback(glfwWindow) { w: Long, c: Int ->
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c)
            }
        }*/

        GLFW.glfwSetMouseButtonCallback(glfwWindow) { w: Long, button: Int, action: Int, mods: Int ->
            val mouseDown = BooleanArray(5)
            mouseDown[0] = button == GLFW.GLFW_MOUSE_BUTTON_1 && action != GLFW.GLFW_RELEASE
            mouseDown[1] = button == GLFW.GLFW_MOUSE_BUTTON_2 && action != GLFW.GLFW_RELEASE
            mouseDown[2] = button == GLFW.GLFW_MOUSE_BUTTON_3 && action != GLFW.GLFW_RELEASE
            mouseDown[3] = button == GLFW.GLFW_MOUSE_BUTTON_4 && action != GLFW.GLFW_RELEASE
            mouseDown[4] = button == GLFW.GLFW_MOUSE_BUTTON_5 && action != GLFW.GLFW_RELEASE
            io.setMouseDown(mouseDown)
            if (!io.wantCaptureMouse && mouseDown[1]) {
                ImGui.setWindowFocus(null)
            }

            if (!io.wantCaptureMouse) {
                MouseListener.mouseButtonCallback(w, button, action, mods)
            }
        }

        GLFW.glfwSetScrollCallback(glfwWindow) { w: Long, xOffset: Double, yOffset: Double ->
            io.mouseWheelH = io.mouseWheelH + xOffset.toFloat()
            io.mouseWheel = io.mouseWheel + yOffset.toFloat()
        }

        io.setSetClipboardTextFn(object : ImStrConsumer() {
            override fun accept(s: String) {
                GLFW.glfwSetClipboardString(glfwWindow, s)
            }
        })

        io.setGetClipboardTextFn(object : ImStrSupplier() {
            override fun get(): String {
                val clipboardString = GLFW.glfwGetClipboardString(glfwWindow)
                return clipboardString ?: ""
            }
        })

        // Fonts
        val fontAtlas = ImGui.getIO().fonts
        val fontConfig = ImFontConfig()

        fontConfig.glyphRanges = fontAtlas.glyphRangesDefault
        fontConfig.pixelSnapH = true
        fontAtlas.addFontFromFileTTF("assets/fonts/Roboto-Medium.ttf", 32f, fontConfig)

        fontConfig.destroy()
    }

    fun imGui(dt: Float, currentScene: Scene) {
        setupDockSpace()
        currentScene.sceneImgui()
        GameViewWindow.imgui()
        ImGui.end()
        ImGui.render()
    }

    private fun setupDockSpace() {
        var windowFlags = ImGuiWindowFlags.MenuBar or ImGuiWindowFlags.NoDocking

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always)
        ImGui.setNextWindowSize(Window.getWidth().toFloat(), Window.getHeight().toFloat())
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f)
        windowFlags = windowFlags or (ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoCollapse or
                ImGuiWindowFlags.NoResize or ImGuiWindowFlags.NoMove or
                ImGuiWindowFlags.NoBringToFrontOnFocus or ImGuiWindowFlags.NoNavFocus)

        ImGui.begin("Dockspace Demo", ImBoolean(true), windowFlags)
        ImGui.popStyleVar(2)

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"))
    }
}