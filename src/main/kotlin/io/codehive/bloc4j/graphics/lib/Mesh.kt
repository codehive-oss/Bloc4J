package io.codehive.bloc4j.graphics.lib

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import java.nio.IntBuffer

class Mesh(
  val gl: GL3,
  positions: FloatArray,
  uvCoords: FloatArray,
  val indices: IntArray
) {

  val vao: IntBuffer = GLBuffers.newDirectIntBuffer(1)
  val ebo: IntBuffer = GLBuffers.newDirectIntBuffer(1)

  val verticesBuffer: VertexBuffer = VertexBuffer(gl, positions, 3)
  val uvBuffer: VertexBuffer = VertexBuffer(gl, uvCoords, 2)

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

    verticesBuffer.build(0)
    uvBuffer.build(1)
  }

  fun render() {
    gl.glBindVertexArray(vao[0])
    gl.glDrawElements(GL.GL_TRIANGLES, indices.size, GL.GL_UNSIGNED_INT, 0)
  }

}