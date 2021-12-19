package physics2d.components

import components.Collider
import marki.renderer.DebugDraw
import org.joml.Vector2f

class Box2DCollider: Collider() {
    val halfSize = Vector2f(1f)
    val origin:Vector2f = Vector2f()

    override fun editorUpdate(dt: Float) {
        val center = Vector2f(gameObject.transform.position).add(offset)
        DebugDraw.addBox2D(center, halfSize, gameObject.transform.rotation)
    }
}