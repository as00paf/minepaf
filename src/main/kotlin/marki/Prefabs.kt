package marki

import components.*
import components.prefabs.Peter
import marki.renderer.Texture
import org.joml.Vector2f
import physics2d.components.RigidBody2D
import physics2d.enums.BodyType
import util.AssetPool

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

    fun generatePeter(): GameObject {
        return Peter().get()
    }

}