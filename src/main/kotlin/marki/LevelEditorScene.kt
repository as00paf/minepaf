package marki

import com.google.gson.GsonBuilder
import components.RigidBody
import components.SpriteRenderer
import components.SpriteSheet
import imgui.ImGui
import marki.renderer.Shader
import marki.renderer.Texture
import org.joml.Vector2f
import org.joml.Vector4f
import util.AssetPool

class LevelEditorScene : Scene() {

    override var camera: Camera = Camera(Vector2f(-250f, 0f))

    lateinit var sprites: SpriteSheet
    val scale = 256
    private val go1 = GameObject("ob1", Transform(Vector2f(0f, 100f), Vector2f(scale.toFloat(), scale.toFloat())), 0)


    override fun init() {
        loadResources()
        if(levelLoaded) {
            activeGameObject = gameObjects[0]
            return
        }

        AssetPool.getSpriteSheet(Texture.PETER_SPRITE)?.let { sprites = it }

        go1.addComponent(SpriteRenderer(sprites.getSprite(spriteIndex)))
        go1.addComponent(RigidBody())
        addGameObjectToScene(go1)

        val go2 = GameObject("obj2", Transform(Vector2f(-100f, 200f), Vector2f(scale.toFloat(), scale.toFloat())), 1)
        go2.addComponent(SpriteRenderer(Vector4f(1f, 0f, 0f, 0.25f)))
        addGameObjectToScene(go2)
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
        //println("FPS: ${1.0f / dt}")

        spriteFlipTimeLeft -= dt
        if (spriteFlipTimeLeft < 0f) {
            spriteFlipTimeLeft = spriteFlipTime
            spriteIndex++
            if (spriteIndex > 5) spriteIndex = 0
            go1.getComponent(SpriteRenderer::class.java)?.setSprite(sprites.getSprite(spriteIndex + 1))
        }

        //go1.transform.position.x += 150 * dt

        gameObjects.forEach { it.update(dt) }
        renderer.render()
    }

    override fun imgui() {

    }

    override fun camera(): Camera = camera
}