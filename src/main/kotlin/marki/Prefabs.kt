package marki

import components.*
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

    private const val PETER_SCALE = 0.33f

    fun generatePeter(): GameObject {
        val sheet = AssetPool.getSpriteSheet(Texture.PETER_SPRITE)!!
        val peter = generateSpriteObject(sheet.getSprite(0), PETER_SCALE, PETER_SCALE, 0, "Peter")

        val idle = AnimationState()
        idle.title = "Idle"
        idle.addFrame(sheet.getSprite(0), 0.1f)
        idle.setLoop(false)

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
        stateMachine.addState(idle)
        stateMachine.addState(run)
        stateMachine.setDefaultState(idle.title)
        stateMachine.addStateTrigger("idle", "run", "startRunning")
        stateMachine.addStateTrigger("run", "idle", "stopRunning")
        peter.addComponent(stateMachine)

        val pb = PillboxCollider()
        pb.init(peter)
        pb.width = 0.39f
        pb.height = 0.31f
        val rb = RigidBody2D()
        rb.init(peter)
        rb.bodyType = BodyType.Dynamic
        rb.continuousCollision = false
        rb.fixedRotation = true
        rb.mass = 25f

        peter.addComponent(rb)
        peter.addComponent(pb)

        val controller = PlayerController()
        controller.init(peter)
        peter.addComponent(controller)

        return peter
    }

}