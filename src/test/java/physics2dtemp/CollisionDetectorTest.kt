package physics2dtemp

import junit.framework.TestCase.assertTrue
import marki.renderer.Line2D
import org.joml.Vector2f
import org.junit.Test
import physics2dtemp.rigidbody.IntersectionDetector2D

class CollisionDetectorTest {

    @Test
    fun pointOnLineShouldReturnTrue() {
        val line = Line2D(to= Vector2f(12f, 4f))
        val point = Vector2f(0f,0f)

        assertTrue(IntersectionDetector2D.pointOnLine(point, line))
    }

    @Test
    fun pointOnLineShouldReturnTrue2() {
        val line = Line2D(to= Vector2f(12f, 4f))
        val point = Vector2f(12f,4f)

        assertTrue(IntersectionDetector2D.pointOnLine(point, line))
    }

    @Test
    fun pointOnVerticalLineShouldReturnTrue() {
        val line = Line2D(to= Vector2f(0f, 10f))
        val point = Vector2f(0f,5f)

        assertTrue(IntersectionDetector2D.pointOnLine(point, line))
    }
}