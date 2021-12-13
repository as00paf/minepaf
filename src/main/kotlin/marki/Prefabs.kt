package marki

import components.Sprite
import components.SpriteRenderer
import org.joml.Vector2f

object Prefabs {

    fun generateSpriteObject(sprite: Sprite, sizeX: Float, sizeY: Float, zIndex: Int = 0): GameObject {
        val go = GameObject(
            "Sprite_Object_Gen", Transform(Vector2f(), Vector2f(sizeX, sizeY)), zIndex)
        val renderer = SpriteRenderer()
        renderer.setSprite(sprite)
        go.addComponent(renderer)

        return go
    }

}