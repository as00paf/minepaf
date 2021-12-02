package marki

import util.extensions.findByClass

class GameObject(
    val name: String,
    var transform: Transform = Transform()
) {

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
        components.add(component)
        component.gameObject = this
    }

    fun update(dt: Float) {
        components.forEach { it.update(dt) }
    }

    fun start(){
        components.forEach { it.start() }
    }
}