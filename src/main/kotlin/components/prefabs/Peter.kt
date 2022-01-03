package components.prefabs

import components.*
import marki.GameObject
import marki.Prefabs
import marki.renderer.Texture
import physics2d.components.RigidBody2D
import physics2d.enums.BodyType
import util.AssetPool

private const val PETER_SCALE = 0.33f

class Peter : GameObject("PeterPrefab") {

    private var go: GameObject

    init {
        val sheet = AssetPool.getSpriteSheet(Texture.PETER_SPRITE)!!
        go = Prefabs.generateSpriteObject(sheet.getSprite(0), PETER_SCALE, PETER_SCALE, 1, "Peter")
        addStateMachine(sheet)
        addRigidBody()
        addPlayerController()
    }

    private fun addStateMachine(sheet: SpriteSheet) {
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
        run.doesLoop = true

        val stateMachine = StateMachine()
        stateMachine.addState(idle)
        stateMachine.addState(run)
        stateMachine.setDefaultState(idle.title)
        stateMachine.addStateTrigger("idle", "run", "startRunning")
        stateMachine.addStateTrigger("run", "idle", "stopRunning")

        go.addComponent(stateMachine)
    }

    private fun addRigidBody() {
        val pb = PillboxCollider()
        pb.init(go)
        pb.width = 0.39f
        pb.height = 0.31f
        val rb = RigidBody2D()
        rb.init(go)
        rb.bodyType = BodyType.Dynamic
        rb.continuousCollision = false
        rb.fixedRotation = true
        rb.mass = 25f

        go.addComponent(rb)
        go.addComponent(pb)
    }

    private fun addPlayerController() {
        val controller = PlayerController()
        controller.init(go)
        go.addComponent(controller)
    }

    fun get(): GameObject {
        return go
    }
}