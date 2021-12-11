package marki

import components.Sprite
import components.SpriteRenderer
import org.joml.Vector2f

object Prefabs {

    fun generateSpriteObject(sprite: Sprite, sizeX: Float, sizeY: Float): GameObject {
        val block = GameObject(
            "Sprite_Object_Gen", Transform(Vector2f(), Vector2f(sizeX, sizeY)), 2)
        val renderer = SpriteRenderer()
        renderer.setSprite(sprite)
        block.addComponent(renderer)

        return block
    }

}