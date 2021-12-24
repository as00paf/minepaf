package components

import util.AssetPool

class AnimationState() {
    var title: String = ""
    val animationFrames = mutableListOf<Frame>()
    var defaultSprite = Sprite()
    @Transient private var timeTracker = 0f
    @Transient private var currentSprite = 0
    var doesloop = false

    fun addFrame(sprite: Sprite, frameTime: Float) {
        animationFrames.add(Frame(sprite, frameTime))
    }

    fun setLoop(loop: Boolean) {
        doesloop = loop
    }

    fun update(dt: Float) {
        if(currentSprite < animationFrames.size) {
            timeTracker -= dt
            if(timeTracker <= 0) {
                if(currentSprite != animationFrames.size - 1 || doesloop) {
                    currentSprite = (currentSprite + 1) % animationFrames.size
                }

                timeTracker = animationFrames[currentSprite].frameTime
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