package marki

import components.Sprite
import components.SpriteRenderer
import org.joml.Vector2f

object Prefabs {

    fun generateSpriteObject(sprite: Sprite, sizeX: Float, sizeY: Float, zIndex: Int = 0): GameObject {
        val go = Window.getScene().createGameObject("Sprite_Object_Gen")
        go.transform.scale = Vector2f(sizeX, sizeY)
        go.transform.zIndex = zIndex
        val renderer = SpriteRenderer()
        renderer.setSprite(sprite)
        go.addComponent(renderer)

        return go
    }

}