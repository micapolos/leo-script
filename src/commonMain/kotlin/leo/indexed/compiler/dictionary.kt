package leo.indexed.compiler

import leo.TypeStructure
import leo.base.indexed
import leo.base.mapFirstOrNull
import leo.base.mapIndexed
import leo.named.compiler.Binding
import leo.named.compiler.Dictionary
import leo.named.compiler.bindingOrNull
import leo.seq

fun Dictionary.indexedBindingOrNull(structure: TypeStructure): IndexedValue<Binding>? =
	definitionStack.seq.mapIndexed.mapFirstOrNull {
		value.bindingOrNull(structure)?.let { binding ->
			index indexed binding
		}
	}