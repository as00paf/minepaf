package marki

import components.Component
import imgui.internal.ImGui
import util.extensions.findByClass

class GameObject(
    val name: String,
) {
    companion object {
        private var ID_COUNTER: Int = 0

        fun init(maxId: Int) {
            ID_COUNTER = maxId
        }
    }

    private var isDead: Boolean = false
    private var doSerialization = true
    private var uId = ID_COUNTER++

    private val components = mutableListOf<Component>()

    @Transient var transform: Transform = Transform()

    fun <T> getComponent(componentClass: Class<T>): T? {
        return components.firstOrNull { componentClass.isAssignableFrom(it.javaClass)} as? T?
    }

    fun <T> removeComponent(componentClass: Class<T>) {
        components.findByClass(componentClass::class)?.let{
            components.remove(it)
        }
    }

    fun addComponent(component: Component): GameObject{
        component.generateId()
        components.add(component)
        component.init(this)
        return this
    }

    fun start(){
        components.forEach { it.start() }
    }

    fun update(dt: Float) {
        components.forEach { it.update(dt) }
    }

    fun editorUpdate(dt: Float) {
        components.forEach { it.editorUpdate(dt) }
    }

    fun imgui() {
        components.forEach {
            if(ImGui.collapsingHeader(it.javaClass.simpleName))
                it.imgui()
        }
    }

    fun getUid() = uId

    fun getAllComponents(): List<Component> = components
    fun setNoSerialize():GameObject {
        doSerialization = false
        return this
    }
    fun doSerialization():Boolean {
        return doSerialization
    }

    fun isDead():Boolean = isDead

    fun destroy() {
        this.isDead = true
        components.forEach { it.destroy() }
    }
}