package marki

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import components.Component
import java.lang.reflect.Type

class GameObjectSerializer: JsonDeserializer<GameObject> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): GameObject {
        val obj = json.asJsonObject
        val name = obj.get("name").asString
        val components = obj.getAsJsonArray("components")
        val transform = context.deserialize<Transform>(obj.get("transform"), Transform::class.java)
        val zIndex = context.deserialize<Int>(obj.get("zIndex"), Int::class.java)

        val go = GameObject(name,transform, zIndex)
        components.forEach { element ->
            val component = context.deserialize<Component>(element, Component::class.java)
            go.addComponent(component)
        }

        return go
    }
}