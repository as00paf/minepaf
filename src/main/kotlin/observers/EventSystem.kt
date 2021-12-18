package observers

import marki.GameObject
import observers.events.Event

object EventSystem {

    private val observers = mutableListOf<Observer>()

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer) {
        if(observers.contains(observer)) {
            observers.remove(observer)
        }
    }

    fun notify(go: GameObject, event: Event) {
        observers.forEach { it.onNotify(go, event) }
    }

}