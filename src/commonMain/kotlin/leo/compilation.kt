package leo

typealias Compilation<T> = Stateful<Compiler, T>

val Syntax.compilation: Compilation<Typed> get() =
	structureCompilation.map { it.typed }

val Syntax.structureCompilation: Compilation<TypedStructure> get() =
	emptyTypedStructure
		.stateful<Compiler, TypedStructure>()
		.foldStateful(lineSeq) { plusTypedStructureCompilation(it) }

fun TypedStructure.plusTypedStructureCompilation(line: SyntaxLine): Compilation<TypedStructure> =
	when (line) {
		is AsSyntaxLine -> TODO()
		is AtomSyntaxLine -> plusTypedStructureCompilation(line.atom)
		is BeSyntaxLine -> TODO()
		is BindSyntaxLine -> TODO()
		is CheckSyntaxLine -> TODO()
		is CommentSyntaxLine -> TODO()
		is DoSyntaxLine -> TODO()
		is DoingSyntaxLine -> TODO()
		is EndSyntaxLine -> TODO()
		is ExampleSyntaxLine -> TODO()
		is FailSyntaxLine -> TODO()
		is GetSyntaxLine -> TODO()
		is GiveSyntaxLine -> TODO()
		is IsSyntaxLine -> TODO()
		is LetSyntaxLine -> TODO()
		is MatchingSyntaxLine -> TODO()
		is PrivateSyntaxLine -> TODO()
		is QuoteSyntaxLine -> TODO()
		is RecurseSyntaxLine -> TODO()
		is RepeatSyntaxLine -> TODO()
		is SetSyntaxLine -> TODO()
		is SwitchSyntaxLine -> TODO()
		is TakeSyntaxLine -> TODO()
		is TestSyntaxLine -> TODO()
		is TrySyntaxLine -> TODO()
		is UpdateSyntaxLine -> TODO()
		is UseSyntaxLine -> TODO()
		is WithSyntaxLine -> TODO()
	}

fun TypedStructure.plusTypedStructureCompilation(atom: SyntaxAtom): Compilation<TypedStructure> =
	when (atom) {
		is FieldSyntaxAtom -> plusTypedStructureCompilation(atom.field)
		is LiteralSyntaxAtom -> plusTypedStructureCompilation(atom.literal)
	}

fun TypedStructure.plusTypedStructureCompilation(field: SyntaxField): Compilation<TypedStructure> =
	field.rhsSyntax.compilation.bind { rhsTyped ->
		expression
			.plus(line(atom(field.name fieldTo rhsTyped)))
			.of(typeStructure.plus(line(atom(field.name fieldTo rhsTyped.type))))
			.ret()
	}

fun TypedStructure.plusTypedStructureCompilation(literal: Literal): Compilation<TypedStructure> =
	expression
		.plus(line(expressionAtom(literal)))
		.of(typeStructure.plus(literal.typeLine))
		.ret()

fun <T> Script.failTypedCompilation(): Compilation<T> =
	Compilation {
		// TODO: Do it properly, capturing the stack trace.
		throw RuntimeException(script(errorName lineTo this).string)
	}