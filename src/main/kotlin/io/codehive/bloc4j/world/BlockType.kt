package io.codehive.bloc4j.world

enum class BlockType() {
  AIR,
  DIRT;
  companion object {
    fun uvFromType(type: BlockType): Array<Float> {
      val a = 1.0f / BlockType.entries.size

      return arrayOf(
          a * type.ordinal, 0.0f,
          a * (type.ordinal + 1), 0.0f,
          a * (type.ordinal + 1), 1.0f,
          a * type.ordinal, 1.0f,
      )
    }
  }
}
