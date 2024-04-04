package io.codehive.bloc4j.world

import com.jogamp.opengl.GL3
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator
import io.codehive.bloc4j.game.Bloc4J
import io.codehive.bloc4j.game.Config
import io.codehive.bloc4j.graphics.lib.Mesh
import org.joml.Vector3f
import org.joml.Vector3i
import kotlin.math.abs

class Chunk(
  private val world: World,
  val coords: Vector3i
) {

  var dirty = true

  private lateinit var mesh: Mesh
  private val data = ByteArray(Config.CHUNK_SIZE * Config.CHUNK_SIZE * Config.CHUNK_SIZE)
  val perlin = PerlinNoiseGenerator.newBuilder().setSeed(69420).build()


  fun fill(type: BlockType) {
    for (i in data.indices) {
      data[i] = type.ordinal.toByte()
    }
  }

  fun generate() {
    for (y in 0..<Config.CHUNK_SIZE) {
      for (z in 0..<Config.CHUNK_SIZE) {
        for (x in 0..<Config.CHUNK_SIZE) {
          val index = y * Config.CHUNK_SIZE * Config.CHUNK_SIZE + z * Config.CHUNK_SIZE + x
          val worldCoords = Vector3i(
            this.coords.x * Config.CHUNK_SIZE + x,
            this.coords.y * Config.CHUNK_SIZE + y,
            this.coords.z * Config.CHUNK_SIZE + z
          )
          data[index] = menger(worldCoords).ordinal.toByte()
        }
      }
    }
  }

  private fun perlin(coords: Vector3i): BlockType {
    val value = perlin.evaluateNoise(
      coords.x.toDouble() / Config.CHUNK_SIZE,
      coords.y.toDouble() / Config.CHUNK_SIZE,
      coords.z.toDouble() / Config.CHUNK_SIZE,
    )
    return if (value > 0) {
      BlockType.STONE
    } else {
      BlockType.AIR
    }
  }

  private fun menger(coords: Vector3i): BlockType {
    val length = world.mengerData[0].size
    coords.x = abs(coords.x % length)
    coords.y = abs(coords.y % length)
    coords.z = abs(coords.z % length)

    return if (world.mengerData[coords.x][coords.y][coords.z]) {
      BlockType.STONE
    } else {
      BlockType.AIR
    }
  }


  private fun positionToIndex(pos: Vector3i): Int {
    return pos.y * Config.CHUNK_SIZE * Config.CHUNK_SIZE + pos.z * Config.CHUNK_SIZE + pos.x
  }

  fun getBlockAt(localX: Int, localY: Int, localZ: Int): BlockType {
    return BlockType.entries[data[positionToIndex(Vector3i(localX, localY, localZ))].toInt()]
  }

  fun distanceFromPlayer(): Int {
    return Vector3i(coords).mul(Config.CHUNK_SIZE).distanceSquared(Bloc4J.player.location.toVec3i())
      .toInt()
  }

  fun recalculate(gl: GL3) {
    val positions: ArrayList<Float> = ArrayList()
    val uvs: ArrayList<Float> = ArrayList()
    val indices: ArrayList<Int> = ArrayList()
    val normals: ArrayList<Float> = ArrayList()
    var currentIndex = 0

    for (y in 0..<Config.CHUNK_SIZE) {
      for (z in 0..<Config.CHUNK_SIZE) {
        for (x in 0..<Config.CHUNK_SIZE) {
          val current = Vector3i(x, y, z)
          val index = positionToIndex(current)
          val type = BlockType.entries[data[index].toInt()]
          if (type == BlockType.AIR) {
            continue
          }
          val offset = Vector3f(
            this.coords.x * Config.CHUNK_SIZE + x.toFloat(),
            this.coords.y * Config.CHUNK_SIZE + y.toFloat(),
            this.coords.z * Config.CHUNK_SIZE + z.toFloat()
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

  fun render() {
    if (!dirty) {
      mesh.render()
    }
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
        this.coords.x * Config.CHUNK_SIZE + neighbourPos.x,
        this.coords.y * Config.CHUNK_SIZE + neighbourPos.y,
        this.coords.z * Config.CHUNK_SIZE + neighbourPos.z,
      )

    if (neighbourPos.x !in 0..<Config.CHUNK_SIZE || neighbourPos.y !in 0..<Config.CHUNK_SIZE || neighbourPos.z !in 0..<Config.CHUNK_SIZE) {
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
