package marki.renderer

import components.Sprite
import components.SpriteRenderer
import marki.Window
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import util.AssetPool

class RenderBatch(private var maxBatchSize: Int) {
    // Vertex
    // ======
    // Pos                  Color                                   Text coords         texId
    // float, float,        float, float, float, float, float,      float, float,       float
    private val sprites = arrayOfNulls<SpriteRenderer>(maxBatchSize)
    private var spriteCount = 0
    private var hasRoom = true
    private var vertices = FloatArray(maxBatchSize * 4 * VERTEX_SIZE)
    private var shader: Shader = AssetPool.getShader(Shader.DEFAULT)
    private var vaoId = -1
    private var vboId = -1
    private val textures = mutableListOf<Texture>()
    private val texSlots = intArrayOf(0, 1, 3, 4, 5, 6, 7)

    fun start() {
        // Generate and bind VAO
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Allocate space for vertices
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertices.size * Float.SIZE_BYTES.toLong(), GL_DYNAMIC_DRAW)

        // Create and upload indices buffer
        val eboId = glGenBuffers()
        val indices = generateIndices()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        // Enable buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET.toLong())
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET.toLong())
        glEnableVertexAttribArray(1)

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET.toLong())
        glEnableVertexAttribArray(2)

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET.toLong())
        glEnableVertexAttribArray(3)
    }

    fun addSprite(sprite: SpriteRenderer) {
        // Get index and add renderObject
        val index = spriteCount
        sprites[index] = sprite
        spriteCount ++

        if(!textures.contains(sprite.getTexture())) {
            textures.add(sprite.getTexture())
        }

        // Add properties to local vertices array
        loadVertexProperties(index)

        if(spriteCount >= maxBatchSize) {
            hasRoom = false
        }
    }

    fun render() {
        var rebufferData = false
        for(index in 0..spriteCount) {
            val sprite = sprites[index]
            if(sprite?.isDirty() == true) {
                loadVertexProperties(index)
                sprite.setClean()
                rebufferData = true
            }
        }

        if(rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboId)
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)
        }

        // Use shader
        shader.use()
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix())
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix())

        textures.forEachIndexed { index, texture ->
            glActiveTexture(GL_TEXTURE0 + index + 1)
            texture.bind()
        }
        shader.uploadIntArray("uTextures", texSlots)

        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glDrawElements(GL_TRIANGLES, spriteCount * 6, GL_UNSIGNED_INT, 0)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        textures.forEach { texture ->
            texture.unbind()
        }

        shader.detach()
    }

    private fun loadVertexProperties(index: Int) {
        val sprite = sprites[index] ?: return

        // Find offset within array (4 vertices per sprite)
        var offset = index * 4 * VERTEX_SIZE
        val color = sprite.getColor()

        val texId = sprite.getTexture().let { sprTex ->
            if(textures.contains(sprTex)) textures.indexOf(sprTex) + 1
            else 0
        }

        val texCoords = sprite.getTextCoords()

        // Add vertices with the appropriate properties
        var xAdd = 1.0f
        var yAdd = 1.0f
        for(i in 0 until 4) {
            when (i) {
                1 -> yAdd = 0f
                2 -> xAdd = 0f
                3 -> yAdd = 1.0f
            }

            // Load position
            val go = sprite.gameObject ?: return
            vertices[offset] = go.transform.position.x + (xAdd * go.transform.scale.x)
            vertices[offset + 1] = go.transform.position.y + (yAdd * go.transform.scale.y)

            // Load color
            vertices[offset + 2] = color.x
            vertices[offset + 3] = color.y
            vertices[offset + 4] = color.z
            vertices[offset + 5] = color.w

            // Load tex coords
            vertices[offset + 6] = texCoords[i].x
            vertices[offset + 7] = texCoords[i].y

            // Load tex id
            vertices[offset + 8] = texId.toFloat()

            offset += VERTEX_SIZE
        }
    }

    private fun generateIndices(): IntArray {
        val elements = IntArray(6 * maxBatchSize)
        for(index in 0 until maxBatchSize){
            loadElementIndices(elements, index)
        }

        return elements
    }

    private fun loadElementIndices(elements: IntArray, index: Int) {
        val offsetArrayIndex = 6 * index
        val offset = 4 * index

        // 3, 2, 0, 0, 2 ,1       7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3
        elements[offsetArrayIndex + 1] = offset + 2
        elements[offsetArrayIndex + 2] = offset + 0

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0
        elements[offsetArrayIndex + 4] = offset + 2
        elements[offsetArrayIndex + 5] = offset + 1
    }

    fun hasRoom() = hasRoom

    fun hasTextureRoom() = textures.size < 8

    fun hasTexture(texture: Texture?) = texture != null && textures.contains(texture)

}

const val POS_SIZE = 2
const val COLOR_SIZE = 4
const val TEX_COORDS_SIZE = 2
const val TEX_ID_SIZE = 1
const val POS_OFFSET = 0
const val COLOR_OFFSET: Int = POS_OFFSET + POS_SIZE * Float.SIZE_BYTES
const val TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.SIZE_BYTES
const val TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.SIZE_BYTES
const val VERTEX_SIZE = 9
const val VERTEX_SIZE_BYTES: Int = VERTEX_SIZE * Float.SIZE_BYTES