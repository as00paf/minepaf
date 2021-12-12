package physics2d.primitives

import org.joml.Vector2f

class RaycastResult {

    private val point = Vector2f()
    private val normal = Vector2f()
    private var t: Float = -1f
    private var hit = false

    fun init(point: Vector2f, normal: Vector2f, t: Float, hit: Boolean) {
        this.point.set(point)
        this.normal.set(normal)
        this.t = t
        this.hit = hit
    }

    companion object {
        fun reset(result: RaycastResult) {
            result.point.zero()
            result.normal.set(0f, 0f)
            result.t = -1f
            result.hit = false
        }
    }
}