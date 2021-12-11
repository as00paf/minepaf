package physics2d.primitives

import org.joml.Vector2f

class Circle(private var radius: Float = 1.0f, private val rigidBody: RigidBody? = null) {
    fun getRadius() = radius
    fun getCenter(): Vector2f {
        return Vector2f(rigidBody?.position)
    }
}