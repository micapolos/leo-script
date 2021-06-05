package leo.expression.compiler

import leo.AsSyntaxLine
import leo.AtomSyntaxLine
import leo.BeSyntaxLine
import leo.BindSyntaxLine
import leo.CheckSyntaxLine
import leo.CommentSyntaxLine
import leo.DoSyntaxLine
import leo.DoingSyntaxLine
import leo.EndSyntaxLine
import leo.ExampleSyntaxLine
import leo.FailSyntaxLine
import leo.FieldSyntaxAtom
import leo.GetSyntaxLine
import leo.GiveSyntaxLine
import leo.IsSyntaxLine
import leo.LetSyntaxLine
import leo.Literal
import leo.LiteralSyntaxAtom
import leo.MatchingSyntaxLine
import leo.PrivateSyntaxLine
import leo.QuoteSyntaxLine
import leo.RecurseSyntaxLine
import leo.RepeatSyntaxLine
import leo.SetSyntaxLine
import leo.Stateful
import leo.SwitchSyntaxLine
import leo.Syntax
import leo.SyntaxAtom
import leo.SyntaxField
import leo.SyntaxLine
import leo.TakeSyntaxLine
import leo.TestSyntaxLine
import leo.TrySyntaxLine
import leo.UpdateSyntaxLine
import leo.UseSyntaxLine
import leo.WithSyntaxLine
import leo.base.notNullOrError
import leo.bind
import leo.expression.expression
import leo.expression.vector
import leo.foldStateful
import leo.lineSeq
import leo.map
import leo.onlyOrNull
import leo.ret
import leo.typeLine
import leo.typeStructure

typealias Compilation<T> = Stateful<Compiler, T>

val <T> T.compilation: Compilation<T> get() = ret()

val Syntax.expressionCompilation: Compilation<CompiledExpression> get() =
	vectorCompilation.bind { compiledVector ->
		compiledVector.typeStructure.lineStack.onlyOrNull.notNullOrError("not an expression").let { typeLine ->
			compiledVector.vector.expressionStack.onlyOrNull!!.of(typeLine).compilation
		}
	}

val Syntax.vectorCompilation: Compilation<CompiledVector> get() =
	vector().of(typeStructure()).compilation.foldStateful(lineSeq) { vectorPlusCompilation(it) }

fun CompiledVector.vectorPlusCompilation(syntaxLine: SyntaxLine): Compilation<CompiledVector> =
	when (syntaxLine) {
		is AsSyntaxLine -> TODO()
		is AtomSyntaxLine -> vectorPlusCompilation(syntaxLine.atom)
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

fun CompiledVector.vectorPlusCompilation(syntaxAtom: SyntaxAtom): Compilation<CompiledVector> =
	when (syntaxAtom) {
		is FieldSyntaxAtom -> vectorPlusCompilation(syntaxAtom.field)
		is LiteralSyntaxAtom -> vectorPlusCompilation(syntaxAtom.literal)
	}

fun CompiledVector.vectorPlusCompilation(field: SyntaxField): Compilation<CompiledVector> =
	field.rhsSyntax.expressionCompilation.map { rhs ->
		vectorPlusExpression(rhs.expression.of(rhs.typeLine))
	}

fun CompiledVector.vectorPlusCompilation(literal: Literal): Compilation<CompiledVector> =
	vectorPlusExpression(expression(literal).of(literal.typeLine)).compilation
