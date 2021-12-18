package physics2d.components

import components.Collider
import org.joml.Vector2f

class Box2DCollider: Collider() {
    val halfSize = Vector2f(1f)
    val origin:Vector2f = Vector2f()
}