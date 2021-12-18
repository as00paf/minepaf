package observers

import marki.GameObject
import observers.events.Event

interface Observer {
    fun onNotify(event: Event, go: GameObject?)
}