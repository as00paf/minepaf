package marki

import imgui.ImGui
import marki.renderer.*
import observers.EventSystem
import observers.Observer
import observers.events.Event
import observers.events.EventType
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.ALCCapabilities
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import scenes.LevelEditorSceneInitializer
import scenes.Scene
import scenes.SceneInitializer
import util.AssetPool
import util.Time


object Window : Observer {
    private val width: Int = 1920
    private val height: Int = 1080
    private val title = "MinePaf"

    private var glfwWindow: Long = -1L

    lateinit var imGuiLayer: ImGuiLayer
    lateinit var currentScene: Scene
    lateinit var frameBuffer: FrameBuffer
    lateinit var pickingTexture: PickingTexture

    // Audio
    var audioContext: Long = -1L
    var audioDevice: Long  = -1L

    private var runtimePlaying = false

    fun get(): Window = this

    fun run() {
        init()
        loop()

        // Destroy audio context
        alcDestroyContext(audioContext)
        alcCloseDevice(audioDevice)

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

        EventSystem.addObserver(this)

        changeScene(LevelEditorSceneInitializer(), true)
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

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make window visible
        glfwShowWindow(glfwWindow)

        initAudio()

        // This is needed for OpenGL
        GL.createCapabilities()

        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)

        frameBuffer = FrameBuffer(1920, 1080)
        glViewport(0, 0, 1920, 1080)
    }

    private fun initAudio() {
        val defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER)
        audioDevice = alcOpenDevice(defaultDeviceName)
        audioContext = alcCreateContext(audioDevice, IntArray(1))
        alcMakeContextCurrent(audioContext)

        val alcCapabilities = ALC.createCapabilities(audioDevice)
        val alCapabilities = AL.createCapabilities(alcCapabilities)

        if(!alCapabilities.OpenAL10) {
            assert(false) { "Error: Audio library not supported" }
        }

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
            glClearColor(1f, 1f, 1f, 1f)
            glClear(GL_COLOR_BUFFER_BIT)

            if (dt >= 0) {
                DebugDraw.draw()
                Renderer.bindShader(defaultShader)
                if (runtimePlaying) currentScene.update(dt)
                else currentScene.editorUpdate(dt)

                currentScene.render()
            }
            frameBuffer.unbind()

            renderImGui(dt, currentScene)

            glfwSwapBuffers(glfwWindow)
            //MouseListener.endFrame()

            endTime = Time.getTime()
            dt = endTime - beginTime
            beginTime = endTime
        }
    }

    private fun renderImGui(dt: Float, currentScene: Scene) {
        imGuiLayer.imGui(dt, currentScene)
    }

    private fun changeScene(initializer: SceneInitializer, isFirstScene: Boolean = false) {
        if (!isFirstScene) currentScene.destroy()

        imGuiLayer.propertiesWindow.activeGameObject = null

        val scene = Scene(initializer)
        currentScene = scene
        scene.load()
        scene.init()
        scene.start()
    }

    override fun onNotify(event: Event, go: GameObject?) {
        when (event.type) {
            EventType.GameEngineStartPlay -> {
                runtimePlaying = true
                currentScene.save()
                changeScene(LevelEditorSceneInitializer(), false)
            }
            EventType.GameEngineStopPlay -> {
                runtimePlaying = false
                changeScene(LevelEditorSceneInitializer(), false)
            }
            EventType.LoadLevel -> changeScene(LevelEditorSceneInitializer(), false)
            EventType.SaveLevel -> currentScene.save()
            EventType.UserEvent -> TODO()
        }
    }

    fun getWidth() = width
    fun getHeight() = height
    fun getTargetAspectRatio() = 16f / 9f

    fun destroy() {
        imGuiLayer.destroy()
        ImGui.destroyContext()
        glfwFreeCallbacks(glfwWindow)
        glfwDestroyWindow(glfwWindow)
        glfwTerminate()
    }
}