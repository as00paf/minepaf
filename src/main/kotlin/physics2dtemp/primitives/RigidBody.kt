package physics2dtemp.primitives

import components.Component
import org.joml.Vector2f
import org.joml.Vector3f

class RigidBody(val position:Vector2f = Vector2f(), val rotation:Float = 0f): Component() {

    private val colliderType: Int = 0
    private val friction = 0.8f
    val velocity = Vector3f(0f, 0.5f, 0f)
    val isHitting = false

}