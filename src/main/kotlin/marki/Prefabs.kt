package marki

import components.AnimationState
import components.Sprite
import components.SpriteRenderer
import components.StateMachine
import marki.renderer.Texture
import org.joml.Vector2f
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

    private const val PETER_SCALE = 0.33f

    fun generatePeter(): GameObject {
        val sheet = AssetPool.getSpriteSheet(Texture.PETER_SPRITE)!!
        val peter = generateSpriteObject(sheet.getSprite(0), PETER_SCALE, PETER_SCALE, 0, "Peter")

        val run = AnimationState()
        run.title = "Run"
        val defaultTimeFrame = 0.23f
        run.addFrame(sheet.getSprite(1), defaultTimeFrame)
        run.addFrame(sheet.getSprite(2), defaultTimeFrame)
        run.addFrame(sheet.getSprite(3), defaultTimeFrame)
        run.addFrame(sheet.getSprite(4), defaultTimeFrame)
        run.addFrame(sheet.getSprite(5), defaultTimeFrame)
        run.addFrame(sheet.getSprite(6), defaultTimeFrame)
        run.addFrame(sheet.getSprite(7), defaultTimeFrame)
        run.doesloop = true

        val stateMachine = StateMachine()
        stateMachine.addState(run)
        stateMachine.setDefaultState(run.title)
        peter.addComponent(stateMachine)

        return peter
    }

}