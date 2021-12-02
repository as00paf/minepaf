package marki.renderer

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.*

class Texture(val filePath: String) {

    val id: Int = glGenTextures()

    private var width: Int = 0
    private var height: Int = 0

    init {
        glBindTexture(GL_TEXTURE_2D, id)

        // Set params
        // Repeat in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        // Pixelate on stretch
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        // Pixelate on shrink
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)
        stbi_set_flip_vertically_on_load(true)
        val image = stbi_load(filePath, width, height, channels, 0)

        if (image != null) {
            this.width = width.get(0)
            this.height = height.get(0)
            if(channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image)
            }else if(channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
            }else{
                assert(false) {"Error: (Texture) Unknown number of channels : '${channels.get(0)}'"}
            }
        } else {
            assert(false) { "Error: (Texture) Unable to load image : $filePath" }
        }

        stbi_image_free(image!!)
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun getWidth() = width
    fun getHeight() = height

    companion object {
        const val PETER_SPRITE = "assets/textures/peter_sprite.png"
        const val PETER = "assets/textures/peter.png"
    }
}