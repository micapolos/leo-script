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

data class Dictionary<out T>(val definitionStack: Stack<Definition<T>>)
fun <T> dictionary(vararg definitions: Definition<T>) = Dictionary(stack(*definitions))

fun <T> Dictionary<T>.plus(typeLine: TypeLine): Dictionary<T> =
	definitionStack.push(typeLine.definition()).let(::Dictionary)

fun <T> Dictionary<T>.plus(structure: TypeStructure): Dictionary<T> =
	fold(structure.lineStack.reverse.seq) { plus(it) }

fun <T> Dictionary<T>.indexedBindingOrNull(structure: TypeStructure): IndexedValue<Binding<T>>? =
	definitionStack.seq.mapIndexed.mapFirstOrNull {
		value.bindingOrNull(structure)?.let { binding ->
			index indexed binding
		}
	}