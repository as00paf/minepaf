package marki

import org.joml.Vector2f

class Transform(
    var position:Vector2f = Vector2f(),
    var scale: Vector2f = Vector2f(),
    var rotation: Float = 0f
) {

    fun copy(): Transform {
        return Transform(Vector2f(this.position), Vector2f(this.scale))
    }

    fun copy(to: Transform) {
        to.position.set(this.position)
        to.scale.set(scale)
    }

    override fun equals(other: Any?): Boolean {
        val transform = other as? Transform ?: return false
        return transform.position == position && transform.scale == scale
    }
}