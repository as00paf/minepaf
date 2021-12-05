package marki

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

class Camera(var position: Vector2f = Vector2f()) {

    private var viewMatrix: Matrix4f = Matrix4f()
    private var projectionMatrix: Matrix4f = Matrix4f()
    private var inverseProjection: Matrix4f = Matrix4f()
    private var inverseView: Matrix4f = Matrix4f()

    init {
        adjustProjection()
    }

    fun adjustProjection() {
        projectionMatrix.identity()
        projectionMatrix.ortho(0.0f, 32f * 40f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f)
        projectionMatrix.invert(inverseProjection)
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
        viewMatrix.invert(inverseView)
        return viewMatrix
    }

    fun getProjectionMatrix(): Matrix4f = projectionMatrix
    fun getInverseProjection() = inverseProjection
    fun getInverseView() = inverseView
}