package marki

import com.google.gson.GsonBuilder
import imgui.ImGui
import marki.renderer.Renderer
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

abstract class Scene {

    protected abstract var camera: Camera
    protected val gameObjects = mutableListOf<GameObject>()
    protected var isRunning = false
    protected val renderer = Renderer()
    protected var activeGameObject: GameObject? = null

    protected var levelLoaded = false
    protected val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Component::class.java, ComponentDeserializer())
        .registerTypeAdapter(GameObject::class.java, GameObjectSerializer())
        .create()

    abstract fun init()

    fun start(){
        gameObjects.forEach { go ->
            go.start()
            renderer.add(go)
        }
        isRunning = true
    }

    fun addGameObjectToScene(gameObject: GameObject) {
        if(!isRunning) {
            gameObjects.add(gameObject)
        }else{
            gameObjects.add(gameObject)
            gameObject.start()
            renderer.add(gameObject)
        }
    }

    abstract fun update(dt: Float)

    abstract fun camera(): Camera

    fun sceneImgui() {
        if(activeGameObject != null) {
            ImGui.begin("Inspector")
            activeGameObject?.imgui()
            ImGui.end()
        }

        imgui()
    }

    open fun imgui() {

    }

    fun saveExit() {
        try {
            val writer = FileWriter("level.txt")
            writer.write(gson.toJson(gameObjects))
            writer.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun load() {
        var inFile = ""
        try {
            inFile = String(Files.readAllBytes(Paths.get("level.txt")))
        }catch (e: IOException){
            e.printStackTrace()
        }

        if(inFile.isNotBlank()){
            val objs:Array<GameObject> = gson.fromJson(inFile, Array<GameObject>::class.java)
            objs.forEach { obj ->
                addGameObjectToScene(obj)
            }

            levelLoaded = true
        }
    }
}