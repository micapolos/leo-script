package leo

val Type.script: Script get() =
	when (this) {
		AnyType -> script(anyName)
		EmptyType -> script()
		is LinkType -> script(link.scriptLink)
	}

val TypeLink.scriptLink: ScriptLink get() =
	type.script linkTo field.scriptLine

val TypeField.scriptLine: ScriptLine get() =
	null
		?: textScriptLineOrNull
		?: numberScriptLineOrNull
		?: name lineTo rhs.script

val TypeField.textScriptLineOrNull: ScriptLine? get() =
	if (name == textName && rhs is NativeTypeRhs && rhs.native.any is String) line(literal(rhs.native.any))
	else null

val TypeField.numberScriptLineOrNull: ScriptLine? get() =
	if (name == numberName && rhs is NativeTypeRhs && rhs.native.any is Number) line(literal(rhs.native.any))
	else null

val TypeRhs.script: Script get() =
	when (this) {
		is NativeTypeRhs -> native.script
		is TypeTypeRhs -> type.script
	}