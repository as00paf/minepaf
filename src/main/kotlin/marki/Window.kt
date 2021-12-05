package marki

import imgui.ImFontConfig
import imgui.gl3.ImGuiImplGl3
import imgui.ImGui
import imgui.flag.ImGuiBackendFlags
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiMouseCursor
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import util.Time
import java.lang.IllegalStateException
import org.lwjgl.glfw.GLFW.GLFW_KEY_Z

import imgui.flag.ImGuiKey

import org.lwjgl.glfw.GLFW.GLFW_KEY_Y

import org.lwjgl.glfw.GLFW.GLFW_KEY_X

import org.lwjgl.glfw.GLFW.GLFW_KEY_V

import org.lwjgl.glfw.GLFW.GLFW_KEY_C

import org.lwjgl.glfw.GLFW.GLFW_KEY_A

import org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER

import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE

import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER

import org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE

import org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE

import org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE

import org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT

import org.lwjgl.glfw.GLFW.GLFW_KEY_END

import org.lwjgl.glfw.GLFW.GLFW_KEY_HOME

import org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN

import org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP

import org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN

import org.lwjgl.glfw.GLFW.GLFW_KEY_UP

import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT

import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT

import org.lwjgl.glfw.GLFW.GLFW_KEY_TAB
import org.lwjgl.glfw.GLFW.glfwGetClipboardString

import imgui.callback.ImStrSupplier

import imgui.callback.ImStrConsumer
import marki.renderer.DebugDraw
import org.lwjgl.glfw.*

import org.lwjgl.glfw.GLFW.glfwSetScrollCallback

import org.lwjgl.glfw.GLFW.GLFW_RELEASE

import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_5

import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_4

import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3

import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2

import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1

import org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback

import org.lwjgl.glfw.GLFW.glfwSetCharCallback

import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER

import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SUPER

import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT

import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT

import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT

import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT

import org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL

import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL

import org.lwjgl.glfw.GLFW.GLFW_PRESS

import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import scenes.LevelEditorScene
import scenes.Scene


object Window {
    private val width: Int = 1920
    private val height: Int = 1080
    private val title = "MinePaf"

    private var glfwWindow: Long = -1L
    private var currentScene: Scene = LevelEditorScene()

    private var r = 1.0f
    private var g = 1.0f
    private var b = 1.0f
    private var a = 1.0f

    // Im GUI
    val imGuiGlfw = ImGuiImplGlfw()
    val imGuiGl3 = ImGuiImplGl3()
    var glslVersion = "#version 130"
    val imGuiLayer = ImGuiLayer()
    val mouseCursors = arrayOfNulls<Long>(ImGuiMouseCursor.COUNT)

    fun get(): Window = this

    fun run() {
        println("Hello LWJGL ${Version.getVersion()} !")
        init()
        loop()

        // Free memory
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    private fun init(){
        initWindow()
        initImGui()
        imGuiGlfw.init(glfwWindow, true)
        imGuiGl3.init(glslVersion)
    }


    private fun initWindow() {
        // Error callback
        GLFWErrorCallback.createPrint(System.err).set()

        if(!glfwInit()) throw IllegalStateException("Unable to initialize GLFW.")

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0)

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL)
        if(glfwWindow == NULL) throw IllegalStateException("Unable to create the GLFW window.")

        // Register input callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback)
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback)
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback)

        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback)

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make window visible
        glfwShowWindow(glfwWindow)

        // This is needed for OpenGL
        GL.createCapabilities()

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        changeScene(0)
    }

    private fun initImGui() {
        ImGui.createContext()

        // IO
        val io = ImGui.getIO()
        io.iniFilename = "imgui.ini"
        io.configFlags = ImGuiConfigFlags.NavEnableKeyboard
        io.backendFlags = ImGuiBackendFlags.HasMouseCursors
        io.backendPlatformName = "imgui_java_impl_glfw"

        // Keyboard mapping
        val keyMap = IntArray(ImGuiKey.COUNT)
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME
        keyMap[ImGuiKey.End] = GLFW_KEY_END
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER
        keyMap[ImGuiKey.A] = GLFW_KEY_A
        keyMap[ImGuiKey.C] = GLFW_KEY_C
        keyMap[ImGuiKey.V] = GLFW_KEY_V
        keyMap[ImGuiKey.X] = GLFW_KEY_X
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z
        io.setKeyMap(keyMap)

        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR)
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)

        // GLFW callbacks to handle user input
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

            if(!io.wantCaptureKeyboard) {
                KeyListener.keyCallback(w, key, scancode, action, mods)
            }
        }

        glfwSetCharCallback(glfwWindow) { w: Long, c: Int ->
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c)
            }
        }

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

            if(!io.wantCaptureMouse){
                MouseListener.mouseButtonCallback(w, button, action, mods)
            }
        }

        glfwSetScrollCallback(glfwWindow) { w: Long, xOffset: Double, yOffset: Double ->
            io.mouseWheelH = io.mouseWheelH + xOffset.toFloat()
            io.mouseWheel = io.mouseWheel + yOffset.toFloat()
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

        // Fonts
        val fontAtlas = ImGui.getIO().fonts
        val fontConfig = ImFontConfig()

        fontConfig.glyphRanges = fontAtlas.glyphRangesDefault
        fontConfig.pixelSnapH = true
        fontAtlas.addFontFromFileTTF("assets/fonts/Roboto-Medium.ttf", 32f, fontConfig)

        fontConfig.destroy()
    }

    fun loop(){
        var beginTime = Time.getTime()
        var endTime:Float
        var dt = -1.0f

        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents()

            DebugDraw.beginFrame()

            glClearColor(r, g, b, a)
            glClear(GL_COLOR_BUFFER_BIT)

            if( dt > 0 ) {
                DebugDraw.draw()
                currentScene.update(dt)
            }

            renderImGui(dt, currentScene)

            glfwSwapBuffers(glfwWindow)

            endTime = Time.getTime()
            dt = endTime - beginTime
            beginTime = endTime
        }

        currentScene.saveExit()
    }

    private fun renderImGui(dt: Float, currentScene: Scene) {
        imGuiGlfw.newFrame()
        ImGui.newFrame()

        imGuiLayer.imGui(dt, currentScene)

        ImGui.render()
        imGuiGl3.renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
        ImGui.endFrame()
    }

    fun changeScene(newScene: Int){
        /*currentScene = when(newScene) {
            0 -> LevelEditorScene()
            1 -> LevelScene()
            else -> {
                assert(false) { "Unknow scene $newScene" }
            }
        }*/

        currentScene.load()
        currentScene.init()
        currentScene.start()

    }

    fun getScene(): Scene = currentScene
    fun getWindowId() = glfwWindow
    fun getWidth() = width
    fun getHeight() = height

    fun destroy(){
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)
        glfwTerminate()
    }
}