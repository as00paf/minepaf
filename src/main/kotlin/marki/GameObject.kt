package marki

import components.Component
import util.extensions.findByClass

class GameObject(
    val name: String,
    var transform: Transform = Transform(),
    private var zIndex:Int = 0
) {
    companion object {
        private var ID_COUNTER: Int = 0

        fun init(maxId: Int) {
            ID_COUNTER = maxId
        }
    }

    private var uId = ID_COUNTER++

    private val components = mutableListOf<Component>()

    fun <T: Component> getComponent(componentClass: Class<T>): T? {
        return components.firstOrNull { componentClass.isAssignableFrom(it.javaClass)} as? T?
    }

    fun <T> removeComponent(componentClass: Class<T>) {
        components.findByClass(componentClass::class)?.let{
            components.remove(it)
        }
    }

    fun addComponent(component: Component){
        component.generateId()
        components.add(component)
        component.gameObject = this
    }

    fun update(dt: Float) {
        components.forEach { it.update(dt) }
    }

    fun start(){
        components.forEach { it.start() }
    }

    fun zIndex() = zIndex

    fun imgui() {
        components.forEach {
            it.imgui()
        }
    }

    fun getUid() = uId

    fun getAllComponents(): List<Component> = components
}