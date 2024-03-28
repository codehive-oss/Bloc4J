package io.codehive.bloc4j.lib;

import static com.jogamp.opengl.GL.GL_FALSE;
import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static com.jogamp.opengl.GL2ES2.GL_LINK_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import io.codehive.bloc4j.Main;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class ShaderProgram {

  private final GL3 gl;
  private final String vertexShaderFile;
  private final String fragmentShaderFile;

  private int shaderProgram;
  private boolean built = false;

  public ShaderProgram(GL3 gl, String vertexShaderFile, String fragmentShaderFile) {
    this.gl = gl;
    this.vertexShaderFile = vertexShaderFile;
    this.fragmentShaderFile = fragmentShaderFile;
  }

  public void build() {
    int vertexShader = createShader(GL_VERTEX_SHADER, vertexShaderFile);
    int fragmentShader = createShader(GL_FRAGMENT_SHADER, fragmentShaderFile);

    shaderProgram = createProgram(vertexShader, fragmentShader);
    built = true;
  }

  public void use() {
    if (!built) {
      throw new IllegalStateException("Attempted using Shader Program before building it");
    }
    gl.glUseProgram(shaderProgram);
  }

  public void delete() {
    gl.glDeleteProgram(shaderProgram);
  }

  private int createProgram(int vertexShader, int fragmentShader) {
    int program = gl.glCreateProgram();
    gl.glAttachShader(program, vertexShader);
    gl.glAttachShader(program, fragmentShader);
    gl.glLinkProgram(program);

    IntBuffer success = GLBuffers.newDirectIntBuffer(1);
    gl.glGetProgramiv(program, GL_LINK_STATUS, success);
    if (success.get(0) == GL_FALSE) {
      IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
      gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, infoLogLength);

      ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
      gl.glGetProgramInfoLog(program, infoLogLength.get(0), null, bufferInfoLog);
      byte[] bytes = new byte[infoLogLength.get(0)];
      bufferInfoLog.get(bytes);
      String strInfoLog = new String(bytes);
      System.err.printf("Error linking Shader Program: %s%n", strInfoLog);
    }
    gl.glDeleteShader(vertexShader);
    gl.glDeleteShader(fragmentShader);
    return program;
  }

  private int createShader(int shaderType, String fileName) {
    int shader = gl.glCreateShader(shaderType);
    String shaderSource = getShaderSourceCode(fileName);
    IntBuffer length = GLBuffers.newDirectIntBuffer(new int[]{shaderSource.length()});
    gl.glShaderSource(shader, 1, new String[]{shaderSource}, length);
    gl.glCompileShader(shader);

    IntBuffer success = GLBuffers.newDirectIntBuffer(1);
    gl.glGetShaderiv(shader, GL_COMPILE_STATUS, success);
    if (success.get(0) == GL_FALSE) {
      IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
      gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, infoLogLength);

      ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
      gl.glGetShaderInfoLog(shader, infoLogLength.get(0), null, bufferInfoLog);
      byte[] bytes = new byte[infoLogLength.get(0)];
      bufferInfoLog.get(bytes);
      String strInfoLog = new String(bytes);
      System.err.printf("Error compiling %s Shader: %s%n", shaderTypeName(shaderType), strInfoLog);
    }
    return shader;
  }

  private String getShaderSourceCode(String fileName) {
    try {
      return IOUtils.toString(Main.class.getResourceAsStream(fileName), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String shaderTypeName(int shaderType) {
    return switch (shaderType) {
      case GL_VERTEX_SHADER -> "Vertex";
      case GL_FRAGMENT_SHADER -> "Fragment";
      default -> "";
    };
  }
}
