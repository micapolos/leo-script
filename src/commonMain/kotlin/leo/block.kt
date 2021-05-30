package leo

enum class BlockType { REPEATEDLY, RECURSIVELY }
data class Block(val typeOrNull: BlockType?, val expression: Expression)

fun BlockType.block(expression: Expression) = Block(this, expression)
fun defaultBlock(expression: Expression) = Block(null, expression)

val String.blockTypeOrNull: BlockType?
	get() =
		when (this) {
			repeatingName -> BlockType.REPEATEDLY
			recursingName -> BlockType.RECURSIVELY
			else -> null
		}

fun block(script: Script): Block =
	typedBlockOrNull(script) ?: defaultBlock(expression(script))

fun typedBlockOrNull(script: Script): Block? =
	script.linkOrNull?.onlyLineOrNull?.fieldOrNull?.let { field ->
		field.string.blockTypeOrNull?.block(expression(field.rhs))
	}

val BlockType.scriptName get() =
	when (this) {
		BlockType.REPEATEDLY -> repeatingName
		BlockType.RECURSIVELY -> recursingName
	}