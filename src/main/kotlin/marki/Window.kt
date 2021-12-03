package marki

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import util.Time
import java.lang.IllegalStateException

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
        // Error callback
        GLFWErrorCallback.createPrint(System.err).set()

        if(!glfwInit()) throw IllegalStateException("Unable to initialize GLFW.")

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE)

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

    fun loop(){
        var beginTime = Time.getTime()
        var endTime = Time.getTime()
        var dt = -1.0f

        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents()

            glClearColor(r, g, b, a)
            glClear(GL_COLOR_BUFFER_BIT)

            if( dt > 0 ) currentScene.update(dt)

            glfwSwapBuffers(glfwWindow)

            endTime = Time.getTime()
            dt = endTime - beginTime
            beginTime = endTime
        }
    }

    fun changeScene(newScene: Int){
        /*currentScene = when(newScene) {
            0 -> LevelEditorScene()
            1 -> LevelScene()
            else -> {
                assert(false) { "Unknow scene $newScene" }
            }
        }*/

        currentScene.init()
        currentScene.start()
    }

    fun getScene(): Scene = currentScene
}