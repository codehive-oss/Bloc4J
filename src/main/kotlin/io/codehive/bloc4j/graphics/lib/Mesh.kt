package io.codehive.bloc4j.graphics.lib

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import java.nio.IntBuffer

class Mesh(
  private val gl: GL3,
  positions: FloatArray,
  uvCoords: FloatArray,
  normals: FloatArray,
  private val indices: IntArray
) {

  private val vao: IntBuffer = GLBuffers.newDirectIntBuffer(1)
  private val ebo: IntBuffer = GLBuffers.newDirectIntBuffer(1)

  private val positionsBuffer = VertexBuffer(gl, positions, 3)
  private val uvBuffer = VertexBuffer(gl, uvCoords, 2)
  private val normals = VertexBuffer(gl, normals, 3)

  fun build() {
    gl.glGenVertexArrays(1, vao)
    gl.glBindVertexArray(vao.get(0))

    gl.glGenBuffers(1, ebo)
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo[0])
    val indicesBuffer = GLBuffers.newDirectIntBuffer(indices)
    gl.glBufferData(
      GL.GL_ELEMENT_ARRAY_BUFFER, (indicesBuffer.capacity() * Integer.BYTES).toLong(),
      indicesBuffer, GL.GL_STATIC_DRAW
    )

    positionsBuffer.build(0)
    uvBuffer.build(1)
    normals.build(2)
  }

  fun render() {
    gl.glBindVertexArray(vao[0])
    gl.glDrawElements(GL.GL_TRIANGLES, indices.size, GL.GL_UNSIGNED_INT, 0)
  }

}
