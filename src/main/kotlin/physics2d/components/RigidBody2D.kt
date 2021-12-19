package physics2d.components

import components.Component
import org.jbox2d.dynamics.Body
import org.joml.Vector2f
import physics2d.enums.BodyType

class RigidBody2D(): Component() {
    val velocity: Vector2f = Vector2f()
    var angularDamping:Float = 0.8f
    var linearDamping:Float = 0.9f
    var mass: Float = 0f
    var bodyType: BodyType = BodyType.Dynamic
    var fixedRotation = false
    var continuousCollision = true
    @Transient var rawBody: Body? = null

    override fun update(dt: Float) {
        val rb = rawBody
        if(rb != null) {
            gameObject.transform.position.set(rb.position.x, rb.position.y)
            gameObject.transform.rotation = Math.toDegrees(rb.angle.toDouble()).toFloat()
        }
    }
}