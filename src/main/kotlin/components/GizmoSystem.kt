package components

import marki.GameObject
import marki.KeyListener
import marki.Window
import org.lwjgl.glfw.GLFW.GLFW_KEY_E
import org.lwjgl.glfw.GLFW.GLFW_KEY_R

class GizmoSystem(private val gizmosSprites: SpriteSheet):Component() {

    private var usingGizmo = TRANSLATE_GIZMO

    override fun init(gameObject:GameObject) {
        super.init(gameObject)
        this.gameObject.addComponent(TranslateGizmo(gizmosSprites.getSprite(1), Window.imGuiLayer.propertiesWindow))
        this.gameObject.addComponent(ScaleGizmo(gizmosSprites.getSprite(2), Window.imGuiLayer.propertiesWindow))
    }

    override fun update(dt: Float) {
        val translateGizmo = gameObject.getComponent(TranslateGizmo::class.java)!!
        val scaleGizmo = gameObject.getComponent(ScaleGizmo::class.java)!!

        if (usingGizmo == TRANSLATE_GIZMO) {
            translateGizmo.setInUse()
            scaleGizmo.setNotInUse()
        } else if (usingGizmo == SCALE_GIZMO) {
            scaleGizmo.setInUse()
            translateGizmo.setNotInUse()
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_E)) {
            println("Using translate gizmo")
            usingGizmo = TRANSLATE_GIZMO
        } else if(KeyListener.isKeyPressed(GLFW_KEY_R)) {
            println("Using scale gizmo")
            usingGizmo = SCALE_GIZMO
        }
    }

    companion object {
        const val TRANSLATE_GIZMO = 0
        const val SCALE_GIZMO = 1
    }
}