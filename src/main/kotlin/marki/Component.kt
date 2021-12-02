package marki

abstract class Component {

    var gameObject: GameObject? = null
    private var isFirstTime = true
    open fun start(){}
    abstract fun update(dt: Float)
}