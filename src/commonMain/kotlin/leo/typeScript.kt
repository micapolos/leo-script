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
	name lineTo rhs.script

val TypeRhs.script: Script get() =
	when (this) {
		is FunctionTypeRhs -> script("#function") // TODO: Fixit
		is KClassTypeRhs -> script("#class") // TODO: Fixit
		is NativeTypeRhs -> script("#native") // TODO: Fixit
		is TypeTypeRhs -> type.script
	}