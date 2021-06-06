package leo.kotlin

import leo.FieldTypeAtom
import leo.LiteralTypeAtom
import leo.Type
import leo.TypeField
import leo.TypeLine
import leo.atomOrNull
import leo.base.effect
import leo.base.orIfNull
import leo.base.orNullIf
import leo.base.titleCase
import leo.bind
import leo.map
import leo.onlyLineOrNull
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
		else newNameGeneration
	}

val TypeField.newNameGeneration: Generation<Name> get() =
	type.onlyLineForNameOrNull
		?.let {
			it.typeNameGeneration.map {
				it.plus(name.titleCase)
			}
		}
		.orIfNull { name.generation }
		.bind { name ->
			name.nameGeneration.bind { generatedName ->
				type.declarationGeneration(generatedName).bind { declaration ->
					typesGeneration.bind { types ->
						types
							.plus(this, GeneratedType(generatedName, kotlin(declaration)))
							.setGeneration
							.map { generatedName }
					}
				}
			}
		}

val TypeField.nameOrNullGeneration: Generation<Name?> get() =
	Generation { it effect it.generatedTypes.get(this)?.name }

val Type.onlyLineForNameOrNull: TypeLine? get() =
	onlyLineOrNull?.orNullIf { atomOrNull !is FieldTypeAtom && atomOrNull !is LiteralTypeAtom }
