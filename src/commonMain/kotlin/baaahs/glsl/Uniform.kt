package baaahs.glsl

import baaahs.geom.*
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import com.danielgergely.kgl.Kgl
import com.danielgergely.kgl.UniformLocation

interface Uniform {
    fun set(x: Int)
    fun set(x: Int, y: Int)
    fun set(x: Int, y: Int, z: Int)
    fun set(x: Float)
    fun set(x: Float, y: Float)
    fun set(x: Float, y: Float, z: Float)
    fun set(x: Float, y: Float, z: Float, w: Float)
    fun set(matrix: Matrix4F)
    fun set(vector2F: Vector2F)
    fun set(vector3F: Vector3F)
    fun set(vector4F: Vector4F)
    fun set(eulerAngle: EulerAngle)
    fun set(textureUnit: GlContext.TextureUnit)
}

class UniformImpl internal constructor(
    private val glslProgram: GlslProgram,
    private val uniformLocation: UniformLocation
): Uniform {
    override fun set(x: Int) = withProgram { uniform1i(uniformLocation, x) }
    override fun set(x: Int, y: Int) = withProgram { uniform2i(uniformLocation, x, y) }
    override fun set(x: Int, y: Int, z: Int) = withProgram { uniform3i(uniformLocation, x, y, z) }
    override fun set(x: Float) = withProgram { uniform1f(uniformLocation, x) }
    override fun set(x: Float, y: Float) = withProgram { uniform2f(uniformLocation, x, y) }
    override fun set(x: Float, y: Float, z: Float) = withProgram { uniform3f(uniformLocation, x, y, z) }
    override fun set(x: Float, y: Float, z: Float, w: Float) = withProgram { uniform4f(uniformLocation, x, y, z, w) }

    override fun set(matrix: Matrix4F) = withProgram {
        uniformMatrix4fv(uniformLocation, false, matrix.elements.map { it.toFloat() }.toFloatArray())
    }

    override fun set(vector2F: Vector2F) = set(vector2F.x, vector2F.y)
    override fun set(vector3F: Vector3F) = set(vector3F.x, vector3F.y, vector3F.z)
    override fun set(vector4F: Vector4F) = set(vector4F.x, vector4F.y, vector4F.z, vector4F.w)
    override fun set(eulerAngle: EulerAngle) =
        set(eulerAngle.xRad.toFloat(), eulerAngle.yRad.toFloat(), eulerAngle.zRad.toFloat())

    override fun set(textureUnit: GlContext.TextureUnit) = textureUnit.setUniform(this)

    private fun <T> withProgram(fn: Kgl.() -> T): T {
        return glslProgram.withProgram(fn)
    }
}
