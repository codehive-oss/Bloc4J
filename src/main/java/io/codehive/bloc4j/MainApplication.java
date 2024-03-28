package io.codehive.bloc4j;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_FRONT_AND_BACK;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL2GL3.GL_LINE;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import io.codehive.bloc4j.lib.GraphicsApplication;
import io.codehive.bloc4j.lib.ShaderProgram;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MainApplication implements GraphicsApplication {

  private boolean shouldQuit = false;

  private IntBuffer vao;
  private ShaderProgram shaderProgram;
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

    shaderProgram = new ShaderProgram(gl, "/shader.vert", "/shader.frag");
    shaderProgram.build();
    shaderProgram.use();

    gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
    gl.glEnableVertexAttribArray(0);
  }

  @Override
  public void display(GL3 gl) {
    gl.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    gl.glClear(GL_COLOR_BUFFER_BIT);

    shaderProgram.use();
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
    shaderProgram.delete();
    gl.glDeleteVertexArrays(1, vao);
  }

  @Override
  public boolean shouldQuit() {
    return shouldQuit;
  }
}
