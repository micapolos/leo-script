package leo.indexed.compiler

import leo.Stack
import leo.TypeLine
import leo.TypeStructure
import leo.base.fold
import leo.base.indexed
import leo.base.mapFirstOrNull
import leo.base.mapIndexed
import leo.push
import leo.reverse
import leo.seq
import leo.stack

data class Dictionary(val definitionStack: Stack<Definition>)
fun dictionary(vararg definitions: Definition) = Dictionary(stack(*definitions))

fun Dictionary.plus(typeLine: TypeLine): Dictionary =
	definitionStack.push(typeLine.definition()).let(::Dictionary)

fun Dictionary.plus(structure: TypeStructure): Dictionary =
	fold(structure.lineStack.reverse.seq) { plus(it) }

fun Dictionary.indexedBindingOrNull(structure: TypeStructure): IndexedValue<Binding>? =
	definitionStack.seq.mapIndexed.mapFirstOrNull {
		value.bindingOrNull(structure)?.let { binding ->
			index indexed binding
		}
	}