package marki

import marki.renderer.Renderer

abstract class Scene {

    protected abstract var camera: Camera
    protected val gameObjects = mutableListOf<GameObject>()
    protected var isRunning = false
    protected val renderer = Renderer()

    abstract fun init()

    fun start(){
        gameObjects.forEach { go ->
            go.start()
            renderer.add(go)
        }
        isRunning = true
    }

    fun addGameObjectToScene(gameObject: GameObject) {
        gameObjects.add(gameObject)
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
}