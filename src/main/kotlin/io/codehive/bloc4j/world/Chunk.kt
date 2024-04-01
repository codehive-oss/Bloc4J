package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import io.codehive.bloc4j.graphics.lib.Mesh
import org.joml.Vector3f
import org.joml.Vector3i

class Chunk(
  private val world: World,
  val coords: Vector3i
) {

  var dirty = true

  private lateinit var mesh: Mesh
  private val data = ByteArray(16 * 16 * 16)

  fun fill(type: BlockType) {
    for (i in data.indices) {
      data[i] = type.ordinal.toByte()
    }
  }

  fun generate(type: BlockType) {
    val perlin = PerlinNoiseGenerator.newBuilder().setSeed(69420).build()

    for (y in 0..<16) {
      for (z in 0..<16) {
        for (x in 0..<16) {
          val index = y * 16 * 16 + z * 16 + x
          val value = perlin.evaluateNoise(
            this.coords.x + x.toDouble() / 16,
            this.coords.y + y.toDouble() / 16,
            this.coords.z + z.toDouble() / 16,
          )
          if (value > 0) {
            data[index] = type.ordinal.toByte()
          } else {
            data[index] = BlockType.AIR.ordinal.toByte()
          }
        }
      }
    }
  }

  private fun positionToIndex(pos: Vector3i): Int {
    return pos.y * 16 * 16 + pos.z * 16 + pos.x
  }

  fun getBlockAt(localX: Int, localY: Int, localZ: Int): BlockType {
    return BlockType.entries[data[positionToIndex(Vector3i(localX, localY, localZ))].toInt()]
  }

  fun recalculate(gl: GL3) {
    val positions: ArrayList<Float> = ArrayList()
    val uvs: ArrayList<Float> = ArrayList()
    val indices: ArrayList<Int> = ArrayList()
    val normals: ArrayList<Float> = ArrayList()
    var currentIndex = 0

    for (y in 0..<16) {
      for (z in 0..<16) {
        for (x in 0..<16) {
          val current = Vector3i(x, y, z)
          val index = positionToIndex(current)
          val type = BlockType.entries[data[index].toInt()]
          if (type == BlockType.AIR) {
            continue
          }
          val offset = Vector3f(
            this.coords.x * 16 + x.toFloat(),
            this.coords.y * 16 + y.toFloat(),
            this.coords.z * 16 + z.toFloat()
          )

          currentIndex =
            appendData(
              positions,
              uvs,
              indices,
              normals,
              current,
              offset,
              currentIndex,
              BlockFace.TOP,
              type
            )
          currentIndex = appendData(
            positions,
            uvs,
            indices, normals,
            current,
            offset,
            currentIndex,
            BlockFace.BOTTOM,
            type
          )
          currentIndex =
            appendData(
              positions,
              uvs,
              indices,
              normals,
              current,
              offset,
              currentIndex,
              BlockFace.EAST,
              type
            )
          currentIndex =
            appendData(
              positions,
              uvs,
              indices,
              normals,
              current,
              offset,
              currentIndex,
              BlockFace.WEST,
              type
            )
          currentIndex = appendData(
            positions,
            uvs,
            indices, normals,
            current,
            offset,
            currentIndex,
            BlockFace.NORTH,
            type
          )
          currentIndex = appendData(
            positions,
            uvs,
            indices, normals,
            current,
            offset,
            currentIndex,
            BlockFace.SOUTH,
            type
          )
        }
      }
    }

    mesh = Mesh(
      gl,
      positions.toFloatArray(),
      uvs.toFloatArray(),
      normals.toFloatArray(),
      indices.toIntArray(),
    )
    mesh.build()
    dirty = false
  }

  fun render(gl: GL3) {
    if (dirty) {
      recalculate(gl)
    }
    mesh.render()
  }

  private fun appendData(
    positions: ArrayList<Float>,
    uvs: ArrayList<Float>,
    indices: ArrayList<Int>,
    normals: ArrayList<Float>,
    index: Vector3i,
    offset: Vector3f,
    currentIndex: Int,
    face: BlockFace,
    type: BlockType
  ): Int {
    val neighbour: BlockType
    val neighbourPos = Vector3i(index).add(face.dir)
    val neighbourPosGlobal =
      Vector3i(
        this.coords.x * 16 + neighbourPos.x,
        this.coords.y * 16 + neighbourPos.y,
        this.coords.z * 16 + neighbourPos.z,
      )

    if (neighbourPos.x !in 0..<16 || neighbourPos.y !in 0..<16 || neighbourPos.z !in 0..<16) {
      neighbour = world.getBlockAtSafe(neighbourPosGlobal)
    } else {
      val neighbourIndex = positionToIndex(neighbourPos)
      neighbour = BlockType.entries[data[neighbourIndex].toInt()]
    }

    // Add more like GLASS etc...
    if (neighbour == BlockType.AIR) {
      createFace(
        positions,
        uvs,
        normals,
        indices,
        offset,
        currentIndex,
        face,
        type
      )

      return currentIndex + 4
    }

    return currentIndex
  }

  private fun createFace(
    positions: ArrayList<Float>,
    uvs: ArrayList<Float>,
    normals: ArrayList<Float>,
    indices: ArrayList<Int>,
    offsets: Vector3f,
    currentIndex: Int,
    face: BlockFace,
    type: BlockType
  ) {
    val x = offsets.x
    val y = offsets.y
    val z = offsets.z

    val newPositions = when (face) {
      BlockFace.TOP -> arrayOf(
        0.5f + x, 0.5f + y, 0.5f + z, // bottom right
        -0.5f + x, 0.5f + y, 0.5f + z, // bottom left
        -0.5f + x, 0.5f + y, -0.5f + z, // top left
        0.5f + x, 0.5f + y, -0.5f + z, // top right
      )

      BlockFace.BOTTOM -> arrayOf(
        -0.5f + x, -0.5f + y, 0.5f + z, // bottom left
        0.5f + x, -0.5f + y, 0.5f + z, // bottom right
        0.5f + x, -0.5f + y, -0.5f + z, // top right
        -0.5f + x, -0.5f + y, -0.5f + z, // top left
      )

      BlockFace.EAST -> arrayOf(
        0.5f + x, -0.5f + y, -0.5f + z, // bottom left
        0.5f + x, -0.5f + y, 0.5f + z, // bottom right
        0.5f + x, 0.5f + y, 0.5f + z, // top right
        0.5f + x, 0.5f + y, -0.5f + z, // top left
      )

      BlockFace.WEST -> arrayOf(
        -0.5f + x, -0.5f + y, 0.5f + z, // bottom left
        -0.5f + x, -0.5f + y, -0.5f + z, // bottom right
        -0.5f + x, 0.5f + y, -0.5f + z, // top right
        -0.5f + x, 0.5f + y, 0.5f + z, // top left
      )

      BlockFace.NORTH -> arrayOf(
        -0.5f + x, -0.5f + y, -0.5f + z, // bottom left
        0.5f + x, -0.5f + y, -0.5f + z, // bottom right
        0.5f + x, 0.5f + y, -0.5f + z, // top right
        -0.5f + x, 0.5f + y, -0.5f + z, // top left
      )

      BlockFace.SOUTH -> arrayOf(
        0.5f + x, -0.5f + y, 0.5f + z, // bottom right
        -0.5f + x, -0.5f + y, 0.5f + z, // bottom left
        -0.5f + x, 0.5f + y, 0.5f + z, // top left
        0.5f + x, 0.5f + y, 0.5f + z, // top right
      )
    }

    val newUvs = BlockType.uvFromType(type)

    val newNormals = arrayOf(
      face.dir.x.toFloat(),
      face.dir.y.toFloat(),
      face.dir.z.toFloat(),

      face.dir.x.toFloat(),
      face.dir.y.toFloat(),
      face.dir.z.toFloat(),

      face.dir.x.toFloat(),
      face.dir.y.toFloat(),
      face.dir.z.toFloat(),

      face.dir.x.toFloat(),
      face.dir.y.toFloat(),
      face.dir.z.toFloat(),
    )

    val newIndices = arrayOf(
      currentIndex + 0,
      currentIndex + 1,
      currentIndex + 2,
      currentIndex + 2,
      currentIndex + 3,
      currentIndex + 0,
    )

    positions.addAll(newPositions)
    uvs.addAll(newUvs)
    normals.addAll(newNormals)
    indices.addAll(newIndices)
  }
}
