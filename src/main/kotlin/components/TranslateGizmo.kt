package components

import editor.PropertiesWindow
import marki.GameObject
import marki.MouseListener
import marki.Prefabs
import marki.Window
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT

class TranslateGizmo(arrowSprite: Sprite, propertiesWindow: PropertiesWindow):Gizmo(arrowSprite, propertiesWindow) {

    override fun update(dt: Float) {
        val go = activeGameObject
        if(go != null) {
            if(xAxisActive && !yAxisActive) go.transform.position.x -= MouseListener.getWorldDx()
            if(yAxisActive) go.transform.position.y -= MouseListener.getWorldDy()
        }

        super.update(dt)
    }
}