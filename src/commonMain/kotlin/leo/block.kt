package leo

enum class BlockType { REPEATEDLY, RECURSIVELY }
data class Block(val typeOrNull: BlockType?, val untypedScript: Script)

fun BlockType.block(script: Script) = Block(this, script)
fun defaultBlock(script: Script) = Block(null, script)

val String.blockTypeOrNull: BlockType?
	get() =
		when (this) {
			repeatingName -> BlockType.REPEATEDLY
			recursingName -> BlockType.RECURSIVELY
			else -> null
		}

fun block(script: Script): Block =
	typedBlockOrNull(script) ?: defaultBlock(script)

fun typedBlockOrNull(script: Script): Block? =
	script.linkOrNull?.onlyLineOrNull?.fieldOrNull?.let { field ->
		field.string.blockTypeOrNull?.block(field.rhs)
	}