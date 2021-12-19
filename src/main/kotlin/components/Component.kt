package components

import editor.MImGui
import imgui.internal.ImGui
import imgui.type.ImInt
import marki.GameObject
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import java.lang.reflect.Modifier

abstract class Component {

    companion object {
        private var ID_COUNTER: Int = 0

        fun init(maxId: Int) {
            ID_COUNTER = maxId
        }
    }

    private var uId = -1

    @Transient
    lateinit var gameObject: GameObject

    open fun init(gameObject:GameObject) {
        this.gameObject = gameObject
    }
    open fun start() {}
    open fun update(dt: Float) {}
    open fun editorUpdate(dt: Float) {}
    open fun imgui() {
        try {
            val fields = this.javaClass.declaredFields
            fields.forEach { field ->
                val isTransient = Modifier.isTransient(field.modifiers)
                if (isTransient) return@forEach
                val isPrivate = Modifier.isPrivate(field.modifiers)
                if (isPrivate) field.isAccessible = true

                val type = field.type
                val value = field.get(this)
                val name = field.name

                when (type) {
                    Int::class.java -> {
                        val typedValue = value as Int
                        field.set(this, MImGui.dragInt(name, typedValue))
                    }
                    Float::class.java -> {
                        val typedValue = value as Float
                        field.set(this, MImGui.dragFloat(name, typedValue))
                    }
                    Boolean::class.java -> {
                        val typedValue = value as Boolean
                        if (ImGui.checkbox("$name : ", typedValue)) {
                            field.set(this, !typedValue)
                        }
                    }
                    Vector2f::class.java -> {
                        val typedValue = value as Vector2f
                        MImGui.drawVec2Control(name, typedValue)
                    }
                    Vector3f::class.java -> {
                        val typedValue = value as Vector3f
                        val imVec = floatArrayOf(typedValue.x, typedValue.y, typedValue.z)
                        if (ImGui.dragFloat3("$name : ", imVec)) {
                            typedValue.set(imVec[0], imVec[1], imVec[2])
                        }
                    }
                    Vector4f::class.java -> {
                        val typedValue = value as Vector4f
                        val imVec = floatArrayOf(typedValue.x, typedValue.y, typedValue.z, typedValue.w)
                        if (ImGui.dragFloat4("$name : ", imVec)) {
                            typedValue.set(imVec[0], imVec[1], imVec[2], imVec[3])
                        }
                    }
                }

                if(type.isEnum) {
                    val enumValues = getEnumValues(type as Class<Enum<*>>)
                    val enumType = (value as Enum<*>).name
                    val index = ImInt(indexOf(enumType, enumValues))
                    if(ImGui.combo(field.name, index, enumValues, enumValues.size)) {
                        field.set(this, type.enumConstants[index.get()])
                    }
                }

                if (isPrivate) field.isAccessible = false
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private fun <T:Enum<T>> getEnumValues(type: Class<T>): Array<String> {
        val enumValues = arrayOfNulls<String>(type.enumConstants.size)
        var i = 0
        for (enumIntegerValue in type.enumConstants) {
            enumValues[i] = enumIntegerValue.name
            i++
        }
        return enumValues.filterNotNull().toTypedArray()
    }

    private fun indexOf(str: String, arr: Array<String>): Int {
        for (i in arr.indices) {
            if (str == arr[i]) {
                return i
            }
        }
        return -1
    }

    fun generateId() {
        if (uId == -1) uId = ID_COUNTER++
    }

    fun getUid() = uId

    open fun destroy() {
    }
}