package components

import marki.Component
import marki.Transform
import org.joml.Vector2f
import org.joml.Vector4f

class SpriteRenderer(
    private var sprite: Sprite,
    private var color: Vector4f = Vector4f(1f, 1f, 1f, 1f)
) : Component() {

    private var lastTransform: Transform = Transform()
    private var isDirty = true

    override fun start() {
        gameObject?.transform?.copy()?.let { this.lastTransform = it }
    }

    override fun update(dt: Float) {
        if(lastTransform != gameObject?.transform && gameObject?.transform != null) {
            gameObject?.transform?.copy(lastTransform)
            isDirty = true
        }
    }

    fun getColor() = color
    fun getTexture() = sprite.getTexture()
    fun getTextCoords(): Array<Vector2f> = sprite.getTexCoords()
    fun isDirty() = isDirty

    fun setSprite(sprite:Sprite) {
        this.sprite = sprite
        isDirty = true
    }

    fun setColor(color: Vector4f) {
        if(this.color != color) {
            this.color.set(color)
            isDirty = true
        }
    }

    fun setClean() { isDirty = false }
}