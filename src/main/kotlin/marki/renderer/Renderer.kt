package marki.renderer

import components.SpriteRenderer
import marki.GameObject

const val MAX_BATCH_SIZE = 1000

class Renderer {
    private val batches = mutableListOf<RenderBatch>()

    fun add(go: GameObject) {
        val spr = go.getComponent(SpriteRenderer::class.java)
        spr?.let { add(it) }
    }

    private fun add(sprite: SpriteRenderer) {
        var added = false
        batches.forEach { batch ->
            if(batch.hasRoom() && batch.zIndex() == sprite.gameObject.transform.zIndex) {
                val texture = sprite.getTexture()
                if(batch.hasTextureRoom() || batch.hasTexture(texture)){
                    batch.addSprite(sprite)
                    added = true
                }
            }
        }

        if(!added) {
            val newBatch = RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.transform.zIndex)
            newBatch.start()
            batches.add(newBatch)
            newBatch.addSprite(sprite)
            batches.sort()
        }
    }

    fun render() {
        currentShader.use()
        batches.forEach { it.render() }
    }

    companion object {
        lateinit var currentShader: Shader

        fun getBoundShader():Shader {
            return currentShader
        }

        fun bindShader(shader: Shader) {
            currentShader = shader
        }
    }
}