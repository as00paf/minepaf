package observers

import marki.GameObject
import observers.events.Event

interface Observer {
    fun onNotify(go: GameObject, event: Event)
}