package leo

import leo.base.Seq
import leo.base.map

data class Definition(val type: Type, val binding: Binding)

fun definition(pattern: Type, binding: Binding) = Definition(pattern, binding)

val Field.setDefinition
	get() =
		definition(
			script(name).type,
			binding(value(this))
		)

val Value.setDefinitionSeq: Seq<Definition>
	get() =
		fieldSeq.map { setDefinition }
