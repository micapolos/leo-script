package leo

typealias TypedCompilation<T> = Stateful<TypedCompiler, T>

val Syntax.typedCompilation: TypedCompilation<Typed> get() =
	typedStructureCompilation.map { it.typed }

val Syntax.typedStructureCompilation: TypedCompilation<TypedStructure> get() =
	emptyTypedStructure
		.stateful<TypedCompiler, TypedStructure>()
		.foldStateful(lineSeq) { plusTypedCompilation(it) }

fun TypedStructure.plusTypedCompilation(line: SyntaxLine): TypedCompilation<TypedStructure> =
	when (line) {
		is AsSyntaxLine -> TODO()
		is AtomSyntaxLine -> plusTypedCompilation(line.atom)
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

fun TypedStructure.plusTypedCompilation(atom: SyntaxAtom): TypedCompilation<TypedStructure> =
	when (atom) {
		is FieldSyntaxAtom -> plusTypedCompilation(atom.field)
		is LiteralSyntaxAtom -> plusTypedCompilation(atom.literal)
	}

fun TypedStructure.plusTypedCompilation(field: SyntaxField): TypedCompilation<TypedStructure> =
	field.rhsSyntax.typedCompilation.bind { rhsTyped ->
		expression
			.plus(line(atom(field.name fieldTo rhsTyped)))
			.of(typeStructure.plus(line(atom(field.name fieldTo rhsTyped.type))))
			.ret()
	}

fun TypedStructure.plusTypedCompilation(literal: Literal): TypedCompilation<TypedStructure> =
	expression
		.plus(line(expressionAtom(literal)))
		.of(typeStructure.plus(literal.typeLine))
		.ret()
