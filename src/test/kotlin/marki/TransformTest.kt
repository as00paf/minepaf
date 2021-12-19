package marki

import junit.framework.TestCase
import org.joml.Vector2f
import org.junit.Test

class TransformTest {
    @Test
    fun transformsWithSameValueShouldReturnTrue() {
        val t1 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 1)
        val t2 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 1)

        TestCase.assertTrue(t1 == t2)
    }

    @Test
    fun transformsWithDifferentPositionsShouldReturnFalse() {
        val t1 = Transform(Vector2f(1f, 1f), Vector2f(1f, 1f), 0.0, 1)
        val t2 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 1)

        TestCase.assertTrue(t1 != t2)
    }

    @Test
    fun transformsWithDifferentScaleShouldReturnFalse() {
        val t1 = Transform(Vector2f(0f, 1f), Vector2f(0.5f, 1f), 0.0, 1)
        val t2 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 1)

        TestCase.assertTrue(t1 != t2)
    }

    @Test
    fun transformsWithDifferentRotationShouldReturnFalse() {
        val t1 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 1.0, 1)
        val t2 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 1)

        TestCase.assertTrue(t1 != t2)
    }

    @Test
    fun transformsWithDifferentZIndexShouldReturnFalse() {
        val t1 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 12)
        val t2 = Transform(Vector2f(0f, 1f), Vector2f(1f, 1f), 0.0, 1)

        TestCase.assertTrue(t1 != t2)
    }
}