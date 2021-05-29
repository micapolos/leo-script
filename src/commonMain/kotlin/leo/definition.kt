package leo

import leo.base.Seq
import leo.base.map

data class Definition(val pattern: Pattern, val binding: Binding)

fun definition(pattern: Pattern, binding: Binding) = Definition(pattern, binding)

val Field.setDefinition
	get() =
		definition(
			pattern(script(name)),
			binding(value(this))
		)

val Value.setDefinitionSeq: Seq<Definition>
	get() =
		fieldSeq.map { setDefinition }
