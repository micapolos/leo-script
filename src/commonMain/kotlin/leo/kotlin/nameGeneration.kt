package leo.kotlin

import leo.TypeField
import leo.base.effect
import leo.base.orIfNull
import leo.bind
import leo.map
import leo.updateAndGetEffect

val String.nameGeneration: Generation<Name> get() =
	Generation { types ->
		types
			.nameCounts
			.updateAndGetEffect(this) { countOrNull -> countOrNull.orIfNull { 0 }.inc() }
			.let { types.copy(nameCounts = it.state) effect Name(this, it.value) }
	}

val TypeField.nameGeneration: Generation<Name> get() =
	nameOrNullGeneration.bind { nameOrNull ->
		if (nameOrNull != null) nameOrNull.generation
		else name.nameGeneration.bind { name ->
			type.declarationGeneration(name).bind { declaration ->
				typesGeneration.bind { types ->
					types
						.plus(this, GeneratedType(name, kotlin(declaration)))
						.setGeneration
						.map { name }
				}
			}
		}
	}

val TypeField.nameOrNullGeneration: Generation<Name?> get() =
	Generation { it effect it.generatedTypes.get(this)?.name }

