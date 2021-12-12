package marki

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.flag.ImGuiMouseCursor
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import marki.renderer.*
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import scenes.LevelEditorScene
import scenes.Scene
import util.AssetPool
import util.Time


object Window {
    private val width: Int = 1920
    private val height: Int = 1080
    private val title = "MinePaf"

    private var glfwWindow: Long = -1L
    private var currentScene: Scene = LevelEditorScene()
    lateinit var frameBuffer: FrameBuffer
    lateinit var pickingTexture: PickingTexture

    private var r = 1.0f
    private var g = 1.0f
    private var b = 1.0f
    private var a = 1.0f

    // Im GUI
    val imGuiGlfw = ImGuiImplGlfw()
    val imGuiGl3 = ImGuiImplGl3()
    var glslVersion = "#version 130"
    lateinit var imGuiLayer:ImGuiLayer
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

    private fun init() {
        initWindow()
        pickingTexture = PickingTexture(1920, 1080)
        imGuiLayer = ImGuiLayer(pickingTexture)
        imGuiLayer.init(glfwWindow)
        imGuiGlfw.init(glfwWindow, true)
        imGuiGl3.init(glslVersion)
    }


    private fun initWindow() {
        // Error callback
        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) throw IllegalStateException("Unable to initialize GLFW.")

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0)

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL)
        if (glfwWindow == NULL) throw IllegalStateException("Unable to create the GLFW window.")

        // Register input callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback)
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback)
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback)

        //glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback)

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

        frameBuffer = FrameBuffer(1920, 1080)
        glViewport(0, 0, 1920, 1080)

        changeScene(0)
    }

    fun loop() {
        var beginTime = Time.getTime()
        var endTime: Float
        var dt = -1.0f

        val defaultShader = AssetPool.getShader(Shader.DEFAULT)
        val pickingShader = AssetPool.getShader(Shader.PICKING)
        while (!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents()

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND)
            pickingTexture.enableWriting()

            glViewport(0, 0, 1920, 1080)
            glClearColor(0f, 0f, 0f, 0f)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            Renderer.bindShader(pickingShader)
            currentScene.render()

            pickingTexture.disableWriting()
            glEnable(GL_BLEND)

            // Render pass 2. Render actual game
            DebugDraw.beginFrame()

            frameBuffer.bind()
            glClearColor(r, g, b, a)
            glClear(GL_COLOR_BUFFER_BIT)

            if (dt > 0) {
                DebugDraw.draw()
                Renderer.bindShader(defaultShader)
                currentScene.update(dt)
                currentScene.render()
            }
            frameBuffer.unbind()

            renderImGui(dt, currentScene)

            glfwSwapBuffers(glfwWindow)

            endTime = Time.getTime()
            dt = endTime - beginTime
            beginTime = endTime
        }

        if (currentScene.saveOnExit) currentScene.saveExit()
    }

    private fun renderImGui(dt: Float, currentScene: Scene) {
        imGuiGlfw.newFrame()
        ImGui.newFrame()

        imGuiLayer.imGui(dt, currentScene)

        imGuiGl3.renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
        ImGui.endFrame()
    }

    fun changeScene(newScene: Int) {
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
    fun getTargetAspectRatio() = 16f / 9f

    fun destroy() {
        imGuiGl3.dispose()
        imGuiGlfw.dispose()
        ImGui.destroyContext()
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)
        glfwTerminate()
    }
}