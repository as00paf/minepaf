package components

import marki.KeyListener
import marki.Window
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import physics2d.components.RigidBody2D
import java.lang.Float.max
import java.lang.Float.min

class PlayerController:Component() {

    var walkSpeed = 1.9f
    var jumpBoost = 1f
    var jumpImpulse = 3f
    var slowDownForce = 0.05f
    private val terminalVelocity = Vector2f(2.1f, 3.1f)

    @Transient private var onGround = false
    @Transient private var groundDebounce = 0f
    @Transient private var groundDebounceTime = 0.1f
    @Transient private var bigJumpBoostFactor = 1.05f
    @Transient private val playerWidth = 0.33f
    @Transient private var jumpTime = 0
    @Transient private val acceleration = Vector2f()
    @Transient private val velocity = Vector2f()
    @Transient private var isDead = false
    @Transient private var enemyBounce = 0

    @Transient lateinit var rb: RigidBody2D
    @Transient lateinit var stateMachine: StateMachine


    override fun start() {
        rb = gameObject.getComponent(RigidBody2D::class.java)!!
        stateMachine = gameObject.getComponent(StateMachine::class.java)!!

        rb.gravityScale = 0f
    }

    //TODO : remove
    override fun editorUpdate(dt: Float) {
        update(dt)
    }

    override fun update(dt: Float) {
        walk(dt)
    }

    private fun walk(dt: Float) {
        if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            gameObject.transform.scale.x = playerWidth
            acceleration.x = walkSpeed

            if(velocity.x < 0) {
                //stateMachine.trigger("switchDirection")
                velocity.x += slowDownForce
            } else {
                stateMachine.trigger("startRunning")
            }

            println("velocity.x : ${velocity.x}")
        } else if(KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            gameObject.transform.scale.x = -playerWidth
            acceleration.x = -walkSpeed

            if(velocity.x > 0) {
                //stateMachine.trigger("switchDirection")
                velocity.x -= slowDownForce
            } else {
                stateMachine.trigger("startRunning")
            }
        } else {
            acceleration.x = 0f
            // Slow down
            if(velocity.x > 0f) {
                velocity.x = max(0f, velocity.x - slowDownForce)
            } else if(velocity.x < 0f) {
                velocity.x = min(0f, velocity.x + slowDownForce)
            }

            if(velocity.x == 0f) stateMachine.trigger("stopRunning")
        }

        acceleration.y = Window.getPhysics().gravity.y * 0.8f
        
        velocity.x += acceleration.x * dt
        velocity.y += acceleration.y * dt

        velocity.x = max(min(velocity.x, terminalVelocity.x), -terminalVelocity.x)
        velocity.y = max(min(velocity.y, terminalVelocity.y), -terminalVelocity.y)

        rb.velocity.set(Vector2f(velocity))
        rb.angularVelocity = 0f
    }
}