package physics2dtemp.primitives

import components.Component
import org.joml.Vector2f

class Collider2D: Component() {
    protected val offset = Vector2f()

    open fun getInertiaTensor() {}
}