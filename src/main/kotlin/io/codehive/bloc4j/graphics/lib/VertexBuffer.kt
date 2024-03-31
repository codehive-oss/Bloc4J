package io.codehive.bloc4j.graphics.lib

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL.GL_ARRAY_BUFFER
import com.jogamp.opengl.GL.GL_STATIC_DRAW
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import java.nio.IntBuffer

class VertexBuffer(
  val gl: GL3,
  val data: FloatArray,
  val elementsPerVertex: Int,
) {

  val vbo: IntBuffer = GLBuffers.newDirectIntBuffer(1)

  fun build(index: Int) {
    gl.glGenBuffers(1, vbo)
    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
    val floatBuffer = GLBuffers.newDirectFloatBuffer(data)
    gl.glBufferData(
      GL_ARRAY_BUFFER, (floatBuffer.capacity() * java.lang.Float.BYTES).toLong(), floatBuffer,
      GL_STATIC_DRAW
    )

    gl.glVertexAttribPointer(
      index,
      elementsPerVertex,
      GL.GL_FLOAT,
      false,
      elementsPerVertex * Float.SIZE_BYTES,
      0
    )
    gl.glEnableVertexAttribArray(index)
  }

}
