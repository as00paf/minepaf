package physics2d.primitives

import org.joml.Vector2f
import util.JMath

class Box2D(val size: Vector2f = Vector2f(), val rigidBody: RigidBody? = null) {

    private val halfSize: Vector2f = Vector2f(size).mul(0.5f)

    fun getMin(): Vector2f {
        return Vector2f(this.rigidBody?.position?.sub(halfSize))
    }

    fun getMax(): Vector2f {
        return Vector2f(this.rigidBody?.position?.add(halfSize))
    }

    fun getPosition(): Vector2f {
        return Vector2f(this.rigidBody?.position)
    }

    fun getRotation(): Float {
        return this.rigidBody?.rotation ?: 0f
    }

    fun getVertices(): Array<Vector2f> {
        val min = getMin()
        val max = getMax()
        val vertices = arrayOf(
            Vector2f(min.x, min.y), Vector2f(min.x, max.y),
            Vector2f(max.x, min.y), Vector2f(max.x, max.y)
        )

        rigidBody ?: return vertices
        if(rigidBody.rotation != 0.0f) {
            vertices.forEach { vert ->
                JMath.rotate(vert, rigidBody.position, rigidBody.rotation.toDouble())
            }
        }

        return vertices
    }

    companion object {
        fun initWithMinMax(min: Vector2f = Vector2f(), max: Vector2f = Vector2f()): Box2D {
            val result = Box2D()
            result.size.set(Vector2f(max).sub(min))
            return result
        }
    }
}