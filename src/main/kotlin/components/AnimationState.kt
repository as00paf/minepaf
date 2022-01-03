package components

import util.AssetPool

class AnimationState {
    var title: String = ""
    val animationFrames = mutableListOf<Frame>()
    var defaultSprite = Sprite()

    @Transient private var time = 0f
    @Transient private var currentSprite = 0
    var doesLoop = false

    fun addFrame(sprite: Sprite, frameTime: Float): AnimationState {
        animationFrames.add(Frame(sprite, frameTime))
        return this
    }

    fun setLoop(loop: Boolean):AnimationState {
        doesLoop = loop
        return this
    }

    fun update(dt: Float) {
        if(currentSprite < animationFrames.size) {
            time -= dt
            if(time <= 0) {
                if (!(currentSprite == animationFrames.size - 1 && !doesLoop)) {
                    currentSprite = (currentSprite + 1) % animationFrames.size
                }

                time = animationFrames[currentSprite].frameTime
            }
        }
    }

    fun getCurrentSprite():Sprite {
        if(currentSprite < animationFrames.size) {
            return animationFrames[currentSprite].sprite
        }

        return defaultSprite
    }

    fun refreshTextures() {
        animationFrames.forEach { frame ->
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture()?.getFilePath().orEmpty()))
        }
    }
}