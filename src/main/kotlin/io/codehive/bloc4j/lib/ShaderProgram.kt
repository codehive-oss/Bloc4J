package io.codehive.bloc4j.lib

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2ES2.*
import com.jogamp.opengl.GL3
import com.jogamp.opengl.util.GLBuffers
import org.apache.commons.io.IOUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

class ShaderProgram(
  private val gl: GL3,
  private val vertexShaderFile: String,
  private val fragmentShaderFile: String
) {
  private var shaderProgram = 0
  private var built = false

  fun build() {
    val vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderFile)
    val fragmentShader = createShader(
      GL_FRAGMENT_SHADER, fragmentShaderFile
    )

    shaderProgram = createProgram(vertexShader, fragmentShader)
    built = true
  }

  fun use() {
    check(built) { "Attempted using Shader Program before building it" }
    gl.glUseProgram(shaderProgram)
  }

  fun delete() {
    gl.glDeleteProgram(shaderProgram)
  }

  private fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
    val program = gl.glCreateProgram()
    gl.glAttachShader(program, vertexShader)
    gl.glAttachShader(program, fragmentShader)
    gl.glLinkProgram(program)

    val success = GLBuffers.newDirectIntBuffer(1)
    gl.glGetProgramiv(program, GL_LINK_STATUS, success)
    if (success[0] == GL.GL_FALSE) {
      val infoLogLength = GLBuffers.newDirectIntBuffer(1)
      gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, infoLogLength)

      val bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength[0])
      gl.glGetProgramInfoLog(program, infoLogLength[0], null, bufferInfoLog)
      val bytes = ByteArray(infoLogLength[0])
      bufferInfoLog.get(bytes)
      val strInfoLog = String(bytes)
      System.err.println("Error linking Shader Program: $strInfoLog")
    }
    gl.glDeleteShader(vertexShader)
    gl.glDeleteShader(fragmentShader)
    return program
  }

  private fun createShader(shaderType: Int, fileName: String): Int {
    val shader = gl.glCreateShader(shaderType)
    val shaderSource = getShaderSourceCode(fileName)
    val length = GLBuffers.newDirectIntBuffer(intArrayOf(shaderSource.length))
    gl.glShaderSource(shader, 1, arrayOf(shaderSource), length)
    gl.glCompileShader(shader)

    val success = GLBuffers.newDirectIntBuffer(1)
    gl.glGetShaderiv(shader, GL_COMPILE_STATUS, success)
    if (success[0] == GL.GL_FALSE) {
      val infoLogLength = GLBuffers.newDirectIntBuffer(1)
      gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, infoLogLength)

      val bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength[0])
      gl.glGetShaderInfoLog(shader, infoLogLength[0], null, bufferInfoLog)
      val bytes = ByteArray(infoLogLength[0])
      bufferInfoLog[bytes]
      val strInfoLog = String(bytes)
      System.err.println("Error compiling ${shaderTypeName(shaderType)} Shader: $strInfoLog")
    }
    return shader
  }

  private fun getShaderSourceCode(fileName: String): String {
    return try {
      IOUtils.toString(
        javaClass.getResourceAsStream(fileName),
        StandardCharsets.UTF_8
      )
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  private fun shaderTypeName(shaderType: Int): String {
    return when (shaderType) {
      GL_VERTEX_SHADER -> "Vertex"
      GL_FRAGMENT_SHADER -> "Fragment"
      else -> ""
    }
  }
}
