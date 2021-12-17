package components

import editor.PropertiesWindow
import marki.MouseListener

class ScaleGizmo(scaleSprite: Sprite, propertiesWindow: PropertiesWindow):Gizmo(scaleSprite, propertiesWindow) {

    override fun update(dt: Float) {
        val go = activeGameObject
        if(go != null) {
            if(xAxisActive && !yAxisActive) go.transform.scale.x -= MouseListener.getWorldDx()
            if(yAxisActive) go.transform.scale.y -= MouseListener.getWorldDy()
        }

        super.update(dt)
    }
}