package leo

object TypeCompiler
typealias TypeCompilation<T> = Stateful<TypeCompiler, T>
val <T> T.typeCompilation: TypeCompilation<T> get() = stateful()

val emptyTypeCompiler = TypeCompiler

fun TypeCompiler.compilation(script: Script): Type =
	script.typeCompilation.run(this).value

val Script.typeCompilation: TypeCompilation<Type> get() =
	if (this == script(anyName)) anyType.typeCompilation
	else when (this) {
		is UnitScript -> emptyType.typeCompilation
		is LinkScript -> link.typeLinkCompilation.map { type(it) }
	}

val ScriptLink.typeLinkCompilation: TypeCompilation<TypeLink> get() =
	line.typeFieldCompilation.bind { typeField ->
		lhs.typeCompilation.map { type ->
			type linkTo typeField
		}
	}

val ScriptLine.typeFieldCompilation: TypeCompilation<TypeField> get() =
	when (this) {
		is FieldScriptLine -> field.typeFieldCompilation
		is LiteralScriptLine -> literal.typeFieldCompilation
	}

val ScriptField.typeFieldCompilation: TypeCompilation<TypeField> get() =
	rhs.typeCompilation.map { type ->
		string fieldTo rhs(type)
	}

val Literal.typeFieldCompilation: TypeCompilation<TypeField> get() =
	when (this) {
		is NumberLiteral -> number.typeFieldCompilation
		is StringLiteral -> string.typeFieldCompilation
	}

val Number.typeFieldCompilation: TypeCompilation<TypeField> get() =
	(numberName fieldTo typeRhs(native(this))).typeCompilation

val String.typeFieldCompilation: TypeCompilation<TypeField> get() =
	(textName fieldTo typeRhs(native(this))).typeCompilation

