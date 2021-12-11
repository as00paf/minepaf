package components

import imgui.internal.ImGui
import marki.Transform
import marki.renderer.Texture
import org.joml.Vector2f
import org.joml.Vector4f

class SpriteRenderer(
    private var sprite: Sprite = Sprite(null),
    private var color: Vector4f = Vector4f(1f, 1f, 1f, 1f)
) : Component() {

    constructor(color: Vector4f): this(Sprite(), color)

    @Transient private var lastTransform: Transform = Transform()
    @Transient private var isDirty = true

    override fun start() {
        this.lastTransform = gameObject.transform.copy()
    }

    override fun update(dt: Float) {
        if(lastTransform != gameObject.transform) {
            gameObject.transform.copy(lastTransform)
            isDirty = true
        }
    }

    override fun imgui() {
        super.imgui()
        val imColor = floatArrayOf(color.x, color.y, color.z, color.w)
        if(ImGui.colorPicker4("Color Picker: ${gameObject.name}", imColor)){
            color.set(imColor[0], imColor[1], imColor[2], imColor[3])
            isDirty = true
        }
    }

    fun getColor() = color
    fun getTextCoords(): Array<Vector2f> = sprite.getTexCoords()
    fun isDirty() = isDirty
    fun getTexture() = sprite.getTexture()
    fun setTexture(tex: Texture) {
        sprite.setTexture(tex)
    }

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