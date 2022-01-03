package components

import marki.GameObject
import marki.KeyListener
import marki.Window
import marki.renderer.DebugDraw
import org.jbox2d.dynamics.contacts.Contact
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import physics2d.components.RigidBody2D
import util.AssetPool
import util.AssetPool.getSound
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

    override fun update(dt: Float) {
        walk(dt)
        jump(dt)
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

    private fun jump(dt: Float) {
        checkOnGround()

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                getSound("assets/sounds/jump-small.ogg")!!.play()
                jumpTime = 28
                velocity.y = jumpImpulse
            } else if (jumpTime > 0) {
                jumpTime--
                velocity.y = jumpTime / 2.2f * jumpBoost
            } else {
                velocity.y = 0f
            }
            groundDebounce = 0f
        } else if (enemyBounce > 0) {
            enemyBounce--
            velocity.y = enemyBounce / 2.2f * jumpBoost
        } else if (!onGround) {
            if (jumpTime > 0) {
                velocity.y *= 0.35f
                jumpTime = 0
            }
            groundDebounce -= dt
            acceleration.y = Window.getPhysics().gravity.y * 0.7f
        } else {
            velocity.y = 0f
            acceleration.y = 0f
            groundDebounce = groundDebounceTime
        }
    }

    fun checkOnGround() {
        val raycastBegin = Vector2f(gameObject.transform.position)
        val innerPlayerWidth = playerWidth * 0.6f
        raycastBegin.sub(innerPlayerWidth / 2f, 0f)
        val yVal = -0.13f
        val raycastEnd = Vector2f(raycastBegin).add(0f, yVal)
        val info = Window.getPhysics().raycast(gameObject, raycastBegin, raycastEnd)

        val raycast2Begin = Vector2f(raycastBegin).add(innerPlayerWidth, 0f)
        val raycast2End = Vector2f(raycastEnd).add(innerPlayerWidth, 0f)
        val info2 = Window.getPhysics().raycast(gameObject, raycast2Begin, raycast2End)

        onGround = (info.hit && info.hitObject != null && info.hitObject?.getComponent(Ground::class.java) != null) ||
                (info.hit && info.hitObject != null && info2.hitObject?.getComponent(Ground::class.java) != null)

        DebugDraw.addLine2D(raycastBegin, raycastEnd, Vector3f(1f, 0f, 0f))
        DebugDraw.addLine2D(raycast2Begin, raycast2End, Vector3f(1f, 0f, 0f))
    }

    override fun beginCollision(go: GameObject, contact: Contact, hitNormal: Vector2f) {
        if (isDead) return

        if (go.getComponent(Ground::class.java) != null) {
            if (Math.abs(hitNormal.x) > 0.8f) {
                velocity.x = 0f
            } else if (hitNormal.y > 0.8f) {
                velocity.y = 0f
                acceleration.y = 0f
                jumpTime = 0
            }
        }
    }

    fun hasWon(): Boolean {
        return false
    }
}