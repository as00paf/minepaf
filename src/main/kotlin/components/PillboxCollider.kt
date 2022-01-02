package components

import marki.GameObject
import marki.Window
import org.joml.Vector2f
import physics2d.components.Box2DCollider
import physics2d.components.CircleCollider
import physics2d.components.RigidBody2D

class PillboxCollider: Component() {

    @Transient val topCircle = CircleCollider()
    @Transient val bottomCircle = CircleCollider()
    @Transient val box = Box2DCollider()
    @Transient private var resetFixtureNextFrame = false

    var width = 0.1f
        set(value) {
            field = value
            recalculateColliders()
            resetFixture()
        }
    var height = 0.2f
        set(value) {
            field = value
            recalculateColliders()
            resetFixture()
        }
    var offset = Vector2f()

    override fun start() {
        this.topCircle.gameObject = gameObject
        this.bottomCircle.gameObject = gameObject
        this.box.gameObject = gameObject
        recalculateColliders()
    }

    override fun update(dt: Float) {
        if(resetFixtureNextFrame) resetFixture()
    }

    override fun editorUpdate(dt: Float) {
        topCircle.editorUpdate(dt)
        bottomCircle.editorUpdate(dt)
        box.editorUpdate(dt)

        if(resetFixtureNextFrame) resetFixture()
    }

    fun resetFixture(){
        if(Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true
            return
        }

        resetFixtureNextFrame = false

        gameObject.getComponent(RigidBody2D::class.java)?.let { rb ->
            Window.getPhysics().resetPillboxCollider(rb, this)
        }
    }

    private fun recalculateColliders() {
        val circleRadius = width / 4f
        val boxHeight = height -2 * circleRadius
        topCircle.radius = circleRadius
        bottomCircle.radius = circleRadius
        topCircle.offset.set(Vector2f(offset).add(0f, boxHeight / 4f))
        bottomCircle.offset.set(Vector2f(offset).sub(0f, boxHeight / 4f))
        box.halfSize.set(Vector2f(width/2f, boxHeight/2f))
        box.offset.set(Vector2f(offset))
    }
}