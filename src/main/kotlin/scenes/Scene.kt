package scenes

import com.google.gson.GsonBuilder
import components.Component
import components.ComponentDeserializer
import imgui.ImGui
import marki.Camera
import marki.GameObject
import marki.GameObjectSerializer
import marki.renderer.Renderer
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

abstract class Scene {

    protected abstract var camera: Camera
    protected val gameObjects = mutableListOf<GameObject>()
    protected var isRunning = false
    val renderer = Renderer()

    var saveOnExit: Boolean = false
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

    fun getGameObject(id: Int): GameObject? {
        return gameObjects.firstOrNull { it.getUid() == id }
    }

    abstract fun update(dt: Float)
    abstract fun render()

    abstract fun camera(): Camera

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
            println("Error: Could not find level.txt")
            //e.printStackTrace()
        }

        if(inFile.isNotBlank()){
            var maxGoId = -1
            var maxCompId = -1
            val objs:Array<GameObject> = gson.fromJson(inFile, Array<GameObject>::class.java)
            objs.forEach { obj ->
                addGameObjectToScene(obj)

                obj.getAllComponents().forEach { component ->
                    if(component.getUid() > maxCompId) {
                        maxCompId = component.getUid()
                    }
                }

                if(obj.getUid() > maxGoId) {
                    maxGoId = obj.getUid()
                }
            }

            maxGoId++
            maxCompId++
            //println(maxGoId)
            //println(maxCompId)
            GameObject.init(maxGoId)
            Component.init(maxCompId)

            levelLoaded = true
        }
    }
}