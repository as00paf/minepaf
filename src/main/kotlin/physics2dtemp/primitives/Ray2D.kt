package physics2dtemp.primitives

import org.joml.Vector2f

class Ray2D(private val origin: Vector2f, private val direction: Vector2f) {

    init {
        direction.normalize()
    }

    fun getOrigin() = origin
    fun getDirection() = direction

}