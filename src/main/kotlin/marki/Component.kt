package marki

abstract class Component {

    lateinit var gameObject: GameObject
    private var isFirstTime = true
    open fun start(){}
    abstract fun update(dt: Float)
}