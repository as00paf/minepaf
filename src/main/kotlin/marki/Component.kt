package marki

import imgui.internal.ImGui
import org.joml.Vector3f
import org.joml.Vector4f
import java.lang.reflect.Modifier

abstract class Component {

    @Transient
    lateinit var gameObject: GameObject

    open fun start() {}
    open fun update(dt: Float) {}
    open fun imgui() {
        try {
            val fields = this.javaClass.declaredFields
            fields.forEach { field ->
                val isTransient = Modifier.isTransient(field.modifiers)
                if(isTransient) return@forEach
                val isPrivate = Modifier.isPrivate(field.modifiers)
                if (isPrivate) field.isAccessible = true

                val type = field.type
                val value = field.get(this)
                val name = field.name

                when (type) {
                    Int::class.java -> {
                        val typedValue = value as Int
                        val imInt = intArrayOf(typedValue)
                        if (ImGui.dragInt("$name: ", imInt)) {
                            field.set(this, imInt[0])
                        }
                    }
                    Float::class.java -> {
                        val typedValue = value as Float
                        val imFloat = floatArrayOf(typedValue)
                        if (ImGui.dragFloat("$name: ", imFloat)) {
                            field.set(this, imFloat[0])
                        }
                    }
                    Boolean::class.java -> {
                        val typedValue = value as Boolean
                        if(ImGui.checkbox("$name : ", typedValue)){
                            field.set(this, !typedValue)
                        }
                    }
                    Vector3f::class.java -> {
                        val typedValue = value as Vector3f
                        val imVec = floatArrayOf(typedValue.x, typedValue.y, typedValue.z)
                        if(ImGui.dragFloat3("$name : ", imVec)) {
                            typedValue.set(imVec[0], imVec[1], imVec[2])
                        }
                    }
                    Vector4f::class.java -> {
                        val typedValue = value as Vector4f
                        val imVec = floatArrayOf(typedValue.x, typedValue.y, typedValue.z, typedValue.w)
                        if(ImGui.dragFloat4("$name : ", imVec)) {
                            typedValue.set(imVec[0], imVec[1], imVec[2], imVec[3])
                        }
                    }
                }
                if (isPrivate) field.isAccessible = false
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}