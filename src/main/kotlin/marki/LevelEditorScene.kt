package marki

import components.SpriteRenderer
import components.SpriteSheet
import marki.renderer.Shader
import marki.renderer.Texture
import org.joml.Vector2f
import util.AssetPool

class LevelEditorScene : Scene() {

    override var camera: Camera = Camera(Vector2f(-250f, 0f))

    lateinit var sprites:SpriteSheet
    private val go1 = GameObject("ob1", Transform(Vector2f(0f, 100f), Vector2f(256f, 256f)))


    override fun init() {
        loadResources()

        sprites = AssetPool.getSpriteSheet(Texture.PETER_SPRITE)!!

        go1.addComponent(SpriteRenderer(sprites!!.getSprite(spriteIndex)))
        addGameObjectToScene(go1)
    }

    private fun loadResources() {
        AssetPool.getShader(Shader.DEFAULT)
        AssetPool.addSpriteSheet(
            Texture.PETER_SPRITE,
            SpriteSheet(AssetPool.getTexture(Texture.PETER_SPRITE), 100, 100, 26, 0)
        )
    }

    private var spriteIndex = 0
    private var spriteFlipTime = 0.2f
    private var spriteFlipTimeLeft = spriteFlipTime

    override fun update(dt: Float) {
        println("FPS: ${1.0f / dt}")

        spriteFlipTimeLeft -= dt
        if(spriteFlipTimeLeft < 0f) {
            spriteFlipTimeLeft = spriteFlipTime
            spriteIndex++
            if(spriteIndex > 5) spriteIndex = 0
            go1.getComponent(SpriteRenderer::class.java)?.setSprite(sprites.getSprite(spriteIndex + 1))
        }

        go1.transform.position.x += 150 * dt

        gameObjects.forEach { it.update(dt) }
        renderer.render()
    }

    override fun camera(): Camera = camera
}