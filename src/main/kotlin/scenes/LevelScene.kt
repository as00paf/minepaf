package scenes

import marki.Camera
import org.joml.Vector2f

class LevelScene: Scene() {

    override var camera: Camera = Camera(Vector2f())

    override fun init() {

    }

    override fun update(dt: Float) {

    }

    override fun camera(): Camera = camera
}