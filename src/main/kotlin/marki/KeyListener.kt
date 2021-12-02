package marki

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object KeyListener {
    fun get(): KeyListener = this

    private var keyPressed = BooleanArray(350)

    fun keyCallback(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) {
        if(key > -1){
            if(action == GLFW_PRESS) {
                this.keyPressed[key] = true
            } else if(action == GLFW_RELEASE){
                this.keyPressed[key] = false
            }
        }
    }

    fun isKeyPressed(key: Int):Boolean = keyPressed[key]
}