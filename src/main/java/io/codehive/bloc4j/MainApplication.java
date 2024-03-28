package io.codehive.bloc4j;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FALSE;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static com.jogamp.opengl.GL2ES2.GL_LINK_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import io.codehive.bloc4j.lib.GraphicsApplication;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MainApplication implements GraphicsApplication {

  private boolean shouldQuit = false;

  private IntBuffer vao;
  private int shaderProgram;
  float[] vertices = {
      0.5f, -0.5f, 0.0f,
      -0.5f, -0.5f, 0.0f,
      -0.5f, 0.5f, 0.0f,
      0.5f, 0.5f, 0.0f
  };

  int[] indices = {
      0, 1, 2,
      2, 3, 0
  };

  String vertexShaderSource = """
      #version 330 core
      layout (location = 0) in vec3 aPos;
            
      void main()
      {
          gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
      }
      """;

  String fragmentShaderSource = """
      #version 330 core
      out vec4 FragColor;
            
      void main()
      {
          FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
      }
      """;

  @Override
  public void init(GL3 gl) {
    vao = GLBuffers.newDirectIntBuffer(1);
    gl.glGenVertexArrays(1, vao);
    gl.glBindVertexArray(vao.get(0));

    IntBuffer ebo = GLBuffers.newDirectIntBuffer(1);
    gl.glGenBuffers(1, ebo);
    gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));

    IntBuffer indicesBuffer = GLBuffers.newDirectIntBuffer(indices);
    gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
        indicesBuffer, GL_STATIC_DRAW);

    IntBuffer vbo = GLBuffers.newDirectIntBuffer(1);
    gl.glGenBuffers(1, vbo);
    gl.glBindBuffer(GL_ARRAY_BUFFER, vbo.get(0));

    FloatBuffer floatBuffer = GLBuffers.newDirectFloatBuffer(vertices);
    gl.glBufferData(GL_ARRAY_BUFFER, floatBuffer.capacity() * Float.BYTES, floatBuffer,
        GL_STATIC_DRAW);

    int vertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
    IntBuffer length = GLBuffers.newDirectIntBuffer(new int[]{vertexShaderSource.length()});
    gl.glShaderSource(vertexShader, 1, new String[]{vertexShaderSource}, length);
    gl.glCompileShader(vertexShader);

    IntBuffer success = GLBuffers.newDirectIntBuffer(1);
    gl.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success);
    if (success.get(0) == GL_FALSE) {
      IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
      gl.glGetShaderiv(vertexShader, GL_INFO_LOG_LENGTH, infoLogLength);

      ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
      gl.glGetShaderInfoLog(vertexShader, infoLogLength.get(0), null, bufferInfoLog);
      byte[] bytes = new byte[infoLogLength.get(0)];
      bufferInfoLog.get(bytes);
      String strInfoLog = new String(bytes);
      System.err.println("Error compiling Vertex Shader: " + strInfoLog);
    }

    int fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
    length.put(0, fragmentShaderSource.length());
    gl.glShaderSource(fragmentShader, 1, new String[]{fragmentShaderSource}, length);
    gl.glCompileShader(fragmentShader);

    gl.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success);
    if (success.get(0) == GL_FALSE) {
      IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
      gl.glGetShaderiv(vertexShader, GL_INFO_LOG_LENGTH, infoLogLength);

      ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
      gl.glGetShaderInfoLog(vertexShader, infoLogLength.get(0), null, bufferInfoLog);
      byte[] bytes = new byte[infoLogLength.get(0)];
      bufferInfoLog.get(bytes);
      String strInfoLog = new String(bytes);
      System.err.println("Error compiling Fragment Shader: " + strInfoLog);
    }

    shaderProgram = gl.glCreateProgram();
    gl.glAttachShader(shaderProgram, vertexShader);
    gl.glAttachShader(shaderProgram, fragmentShader);
    gl.glLinkProgram(shaderProgram);

    gl.glGetProgramiv(shaderProgram, GL_LINK_STATUS, success);
    if (success.get(0) == GL_FALSE) {
      IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
      gl.glGetProgramiv(shaderProgram, GL_INFO_LOG_LENGTH, infoLogLength);

      ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
      gl.glGetProgramInfoLog(shaderProgram, infoLogLength.get(0), null, bufferInfoLog);
      byte[] bytes = new byte[infoLogLength.get(0)];
      bufferInfoLog.get(bytes);
      String strInfoLog = new String(bytes);
      System.err.println("Error linking Shader Program: " + strInfoLog);
    }

    gl.glUseProgram(shaderProgram);
    gl.glDeleteShader(vertexShader);
    gl.glDeleteShader(fragmentShader);

    gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
    gl.glEnableVertexAttribArray(0);
  }

  @Override
  public void display(GL3 gl) {
    gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    gl.glClear(GL_COLOR_BUFFER_BIT);

    gl.glUseProgram(shaderProgram);
    gl.glBindVertexArray(vao.get(0));
    gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    gl.glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
  }

  @Override
  public void reshape(GL3 gl, int width, int height) {
    gl.glViewport(0, 0, width, height);
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
      shouldQuit = true;
    }
  }

  @Override
  public void end(GL3 gl) {
    gl.glDeleteProgram(shaderProgram);
    gl.glDeleteVertexArrays(1, vao);
  }

  @Override
  public boolean shouldQuit() {
    return shouldQuit;
  }
}
