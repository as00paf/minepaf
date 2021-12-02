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
            if(batch.hasRoom()) {
                val texture = sprite.getTexture()
                if(batch.hasTextureRoom() || batch.hasTexture(texture)){
                    batch.addSprite(sprite)
                    added = true
                }
            }
        }

        if(!added) {
            val newBatch = RenderBatch(MAX_BATCH_SIZE)
            newBatch.start()
            batches.add(newBatch)
            newBatch.addSprite(sprite)
        }
    }

    fun render() {
        batches.forEach { it.render() }
    }
}