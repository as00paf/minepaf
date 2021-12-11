package physics2d.primitives

import components.Component
import org.joml.Vector2f

class Collider2D: Component() {
    protected val offset = Vector2f()

    open fun getInertiaTensor() {}
}