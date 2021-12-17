package marki

import components.Component
import editor.MImGui
import org.joml.Vector2f

class Transform(
    var position:Vector2f = Vector2f(),
    var scale: Vector2f = Vector2f(),
    var rotation: Float = 0f,
    var zIndex:Int = 0
): Component() {

    override fun imgui() {
        MImGui.drawVec2Control("Position", position)
        MImGui.drawVec2Control("Scale", scale, 32f)
        MImGui.dragFloat("Rotation", rotation)
        MImGui.dragInt("zIndex", zIndex)
    }

    fun copy(): Transform {
        return Transform(Vector2f(this.position), Vector2f(this.scale), this.rotation, this.zIndex)
    }

    fun copy(to: Transform) {
        to.position.set(this.position)
        to.scale.set(scale)
        to.rotation = this.rotation
        to.zIndex = this.zIndex
    }

    override fun equals(other: Any?): Boolean {
        val transform = other as? Transform ?: return false
        return transform.position == position && transform.scale == scale && other.rotation == transform.rotation && transform.zIndex == other.zIndex
    }
}