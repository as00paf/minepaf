package marki

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class Camera(var position: Vector2f = Vector2f(0f, 0f)) {

    private var viewMatrix: Matrix4f = Matrix4f()
    private var projectionMatrix: Matrix4f = Matrix4f()
    private var inverseProjection: Matrix4f = Matrix4f()
    private var inverseView: Matrix4f = Matrix4f()
    private val projectionWidth = 6f
    private val projectionHeight = 3f
    private val projectionSize = Vector2f(projectionWidth, projectionHeight)
    var zoom = 1f

    init {
        adjustProjection()
    }

    fun adjustProjection() {
        projectionMatrix.identity()
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100.0f)
        inverseProjection = Matrix4f(projectionMatrix).invert()
    }

    fun getViewMatrix(): Matrix4f {
        val cameraFront = Vector3f(0.0f, 0.0f, -1.0f)
        val cameraUp = Vector3f(0.0f, 1.0f, 0.0f)
        viewMatrix.identity()
        viewMatrix.lookAt(
            Vector3f(position.x, position.y, 20.0f),
            cameraFront.add(position.x, position.y, 0.0f),
            cameraUp
        )
        inverseView = Matrix4f(viewMatrix).invert()
        return viewMatrix
    }

    fun addZoom(value: Float) {
        zoom += value
    }

    fun getProjectionMatrix(): Matrix4f = projectionMatrix
    fun getInverseProjection() = inverseProjection
    fun getInverseView() = inverseView
    fun getProjectionSize() = projectionSize
}