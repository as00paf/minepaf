package components

import marki.renderer.Texture
import org.joml.Vector2f

class Sprite(
    private var texture: Texture? = null,
    private var texCoords:Array<Vector2f> = arrayOf(
    Vector2f(1f, 1f),
    Vector2f(1f, 0f),
    Vector2f(0f, 0f),
    Vector2f(0f, 1f)
    )
) {

    fun getTexture() = texture
    fun getTexCoords() = texCoords


}