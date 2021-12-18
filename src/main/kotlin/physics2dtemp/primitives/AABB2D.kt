package physics2dtemp.primitives

import org.joml.Vector2f

// Axis Aligned Bounding Box
class AABB2D(private val size:Vector2f = Vector2f(), private val rigidBody: RigidBody = RigidBody()) {

    private val halfSize:Vector2f = Vector2f(size).mul(0.5f)

    companion object {
        fun initWithMinMax(min:Vector2f = Vector2f(), max:Vector2f = Vector2f()): AABB2D {
            val result = AABB2D()
            result.size.set(Vector2f(max).sub(min))
            return result
        }
    }

    fun getMin():Vector2f {
        return Vector2f(this.rigidBody.position.sub(halfSize))
    }

    fun getMax():Vector2f {
        return Vector2f(this.rigidBody.position.add(halfSize))
    }
}