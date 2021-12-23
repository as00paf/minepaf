package marki

import components.Sprite
import components.SpriteRenderer
import org.joml.Vector2f

object Prefabs {

    fun generateSpriteObject(sprite: Sprite, sizeX: Float, sizeY: Float, zIndex: Int = 0, name: String = "Sprite_Object_Gen"): GameObject {
        val go = Window.currentScene.createGameObject(name)
        go.transform.scale = Vector2f(sizeX, sizeY)
        go.transform.zIndex = zIndex
        val renderer = SpriteRenderer()
        renderer.setSprite(sprite)
        go.addComponent(renderer)

        return go
    }

}