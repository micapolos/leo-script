package leo

enum class BlockType { REPEATEDLY, RECURSIVELY }
data class Block(val typeOrNull: BlockType?, val syntax: Syntax)

fun BlockType.block(syntax: Syntax) = Block(this, syntax)

val String.blockTypeOrNull: BlockType?
	get() =
		when (this) {
			repeatingName -> BlockType.REPEATEDLY
			recursingName -> BlockType.RECURSIVELY
			else -> null
		}

val BlockType.scriptName get() =
	when (this) {
		BlockType.REPEATEDLY -> repeatingName
		BlockType.RECURSIVELY -> recursingName
	}