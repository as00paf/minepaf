package scenes

import components.*
import imgui.ImGui
import imgui.ImVec2
import marki.*
import marki.renderer.Shader
import marki.renderer.Texture
import org.joml.Vector2f
import util.AssetPool

class LevelEditorScene : Scene() {

    override var camera: Camera = Camera(Vector2f(-0f, 0f))

    lateinit var peterSprites: SpriteSheet
    lateinit var blocksSprites: SpriteSheet
    lateinit var gizmosSprites: SpriteSheet
    private val levelEditorStuff = GameObject("LevelEditor")

    override fun init() {
        saveOnExit = true
        loadResources()

        levelEditorStuff.addComponent(MouseControls())
        levelEditorStuff.addComponent(GridLines())
        levelEditorStuff.addComponent(EditorCamera(camera))
        levelEditorStuff.addComponent(GizmoSystem(gizmosSprites))
        levelEditorStuff.start()
    }

    private fun loadResources() {
        AssetPool.getShader(Shader.DEFAULT)
        AssetPool.addSpriteSheet(
            Texture.PETER_SPRITE,
            SpriteSheet(AssetPool.getTexture(Texture.PETER_SPRITE), 100, 100, 26, 0)
        )
        AssetPool.addSpriteSheet(
            Texture.BLOCKS_DECOS_SPRITE,
            SpriteSheet(AssetPool.getTexture(Texture.BLOCKS_DECOS_SPRITE), 32, 32, 14, 0)
        )
        AssetPool.addSpriteSheet(
            Texture.GIZMOS_SPRITE,
            SpriteSheet(AssetPool.getTexture(Texture.GIZMOS_SPRITE), 24, 48, 3, 0)
        )

        AssetPool.getSpriteSheet(Texture.PETER_SPRITE)?.let { peterSprites = it }
        AssetPool.getSpriteSheet(Texture.BLOCKS_DECOS_SPRITE)?.let { blocksSprites = it }
        AssetPool.getSpriteSheet(Texture.GIZMOS_SPRITE)?.let { gizmosSprites = it }

        gameObjects.forEach { go ->
            val spriteRenderer = go.getComponent(SpriteRenderer::class.java)
            val texture = spriteRenderer?.getTexture()
            if(texture != null) {
                val path = texture.getFilePath()!!
                spriteRenderer.setTexture(AssetPool.getTexture(path))
            }
        }
    }

    override fun update(dt: Float) {
        // println("FPS: ${1.0f / dt}")

        levelEditorStuff.update(dt)
        camera.adjustProjection()

        gameObjects.forEach { it.update(dt) }
        render()
    }

    override fun render() {
        renderer.render()
    }

    override fun imgui() {
        ImGui.begin("Level Editor Stuff")
        levelEditorStuff.imgui()
        ImGui.end()

        ImGui.begin("Blocks")

        val windowPos = ImVec2()
        ImGui.getWindowPos(windowPos)
        val windowSize = ImVec2()
        ImGui.getWindowSize(windowSize)
        val itemSpacing = ImVec2()
        ImGui.getStyle().getItemSpacing(itemSpacing)

        val textureScale = 2

        val windowX2 = windowPos.x + windowSize.x
        for (i in 0 until blocksSprites.size()) {
            val sprite = blocksSprites.getSprite(i)
            val spriteWidth = sprite.width * textureScale
            val spriteHeight = sprite.height * textureScale
            val id = sprite.getTexId()
            val texCoords = sprite.getTexCoords()

            ImGui.pushID(i)
            if (ImGui.imageButton(
                    id,
                    spriteWidth,
                    spriteHeight,
                    texCoords[2].x,
                    texCoords[0].y,
                    texCoords[0].x,
                    texCoords[2].y
                )
            ) {
                println("Button $i clicked")
                val block = Prefabs.generateSpriteObject(sprite, 32f, 32f)
                levelEditorStuff.getComponent(MouseControls::class.java)?.pickUpObject(block)
            }
            ImGui.popID()

            val lastButtonPos = ImVec2()
            ImGui.getItemRectMax(lastButtonPos)
            val lastButtonX2 = lastButtonPos.x
            val nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth
            if (i + 1 < blocksSprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine()
            }
        }

        ImGui.end()

    }

    override fun camera(): Camera = camera
}