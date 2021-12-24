package components

import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImString
import java.util.*


class StateMachine : Component() {
    class StateTrigger(var state: String = "", var trigger: String = "") {

        override fun equals(other: Any?): Boolean {
            return if (other !is StateTrigger) false
            else other.state == state && other.trigger == trigger
        }

        override fun hashCode(): Int {
            return Objects.hash(trigger, state)
        }
    }

    private val stateTransfers = HashMap<StateTrigger, String>()
    private val states = mutableListOf<AnimationState>()
    @Transient
    private var currentState: AnimationState? = null
    private var defaultStateTitle = ""


    fun refreshTextures() {
        states.forEach { state ->
            state.refreshTextures()
        }
    }

    fun addState(state: AnimationState) {
        this.states.add(state)
    }

    fun setDefaultState(animationTitle: String) {
        states.forEach { state ->
            if (state.title == animationTitle) {
                defaultStateTitle = animationTitle
                if (currentState == null) {
                    currentState = state
                    return@setDefaultState
                }
            }
        }
    }

    fun addStateTrigger(from: String, to: String, onTrigger: String) {
        this.stateTransfers[StateTrigger(from, onTrigger)] = to
    }

    fun trigger(trigger: String) {
        for (state in stateTransfers.keys) {
            if (state.state == currentState?.title && state.trigger == trigger) {
                if (stateTransfers[state] != null) {
                    val newStateIndex = states.indexOf(stateTransfers[state])
                    if (newStateIndex > -1) {
                        currentState = states[newStateIndex]
                    }
                }
                return
            }
        }

        System.out.println("Unable to find trigger '$trigger'")
    }

    override fun start() {
        for (state in states) {
            if (state.title == defaultStateTitle) {
                currentState = state
                break
            }
        }
    }

    override fun update(dt: Float) {
        if (currentState != null) {
            currentState?.update(dt)
            val sprite = gameObject.getComponent(SpriteRenderer::class.java)
            sprite?.setSprite(currentState?.getCurrentSprite()!!)
        }
    }

    override fun editorUpdate(dt: Float) {
        if (currentState != null) {
            currentState?.update(dt)
            val sprite = gameObject.getComponent(SpriteRenderer::class.java)
            sprite?.setSprite(currentState?.getCurrentSprite()!!)
        }
    }

    override fun imgui() {
        for (state in states) {
            val title = ImString(state.title)
            ImGui.inputText("State: ", title)
            state.title = title.get()

            val doesLoop = ImBoolean(state.doesloop)
            ImGui.checkbox("Does loop: ", doesLoop)

            state.setLoop(doesLoop.get())
            for ((index, frame) in state.animationFrames.withIndex()) {
                val tmp = FloatArray(1)
                tmp[0] = frame.frameTime
                ImGui.dragFloat("Frame($index) Time: ", tmp, 0.01f)
                frame.frameTime = tmp[0]
            }
        }
    }
}