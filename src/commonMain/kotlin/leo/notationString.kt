package leo

import leo.base.*

val Notation.string get() = appendableString { it.append(this) }.addMissingNewline

fun Appendable.append(notation: Notation): Appendable =
	indented.append(notation).appendable

fun AppendableIndented.append(notation: Notation): AppendableIndented =
	when (notation) {
		EmptyNotation -> this
		is LinkNotation -> append(notation.link)
	}

fun AppendableIndented.append(link: NotationLink): AppendableIndented =
	append(link.lhs).runIf(link.lhs !is EmptyNotation) { append('\n') }.append(link.line)

fun AppendableIndented.append(line: NotationLine): AppendableIndented =
	when (line) {
		is ChainNotationLine -> append(line.chain)
		is FieldNotationLine -> append(line.field)
	}

fun AppendableIndented.append(field: NotationField): AppendableIndented =
	append(field.name).appendRhs(field.notationLink)

fun AppendableIndented.appendRhs(link: NotationLink): AppendableIndented =
	when (link.lhs) {
		EmptyNotation -> append(' ').append(link.line)
		is LinkNotation -> indented { append('\n').append(link) }
	}

fun AppendableIndented.append(chain: Chain): AppendableIndented =
	when (chain) {
		is AtomChain -> append(chain.atom)
		is LinkChain -> append(chain.link)
	}

fun AppendableIndented.append(link: ChainLink): AppendableIndented =
	append(link.lhsChain).append('.').append(link.rhsName)

fun AppendableIndented.append(atom: Atom): AppendableIndented =
	when (atom) {
		is LiteralAtom -> append(atom.literal)
		is NameAtom -> append(atom.name)
	}

fun AppendableIndented.append(literal: Literal): AppendableIndented =
	append(literal.toString())
