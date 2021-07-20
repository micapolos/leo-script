package leo

import leo.base.fold
import leo.base.notNullIf
import leo.base.runIfNotNull

sealed class Notation
object EmptyNotation : Notation()
data class LinkNotation(val link: NotationLink) : Notation()

sealed class Atom
data class LiteralAtom(val literal: Literal) : Atom()
data class NameAtom(val name: String) : Atom()

sealed class Chain
data class AtomChain(val atom: Atom) : Chain()
data class LinkChain(val link: ChainLink) : Chain()

sealed class NotationLine
data class ChainNotationLine(val chain: Chain) : NotationLine()
data class FieldNotationLine(val field: NotationField) : NotationLine()

data class ChainLink(val lhsChain: Chain, val rhsName: String)
data class NotationField(val name: String, val notationLink: NotationLink)
data class NotationLink(val lhs: Notation, val line: NotationLine)

val emptyNotation: Notation = EmptyNotation
fun notation(link: NotationLink): Notation = LinkNotation(link)
fun Notation.plus(line: NotationLine): Notation = notation(this linkTo line)
infix fun Notation.linkTo(line: NotationLine) = NotationLink(this, line)
infix fun String.fieldTo(link: NotationLink) = NotationField(this, link)
fun notation(vararg lines: NotationLine) = emptyNotation.fold(lines) { plus(it) }

fun atom(literal: Literal): Atom = LiteralAtom(literal)
fun atom(name: String): Atom = NameAtom(name)

fun line(chain: Chain): NotationLine = ChainNotationLine(chain)
fun line(field: NotationField): NotationLine = FieldNotationLine(field)

fun chain(atom: Atom): Chain = AtomChain(atom)
fun chain(link: ChainLink): Chain = LinkChain(link)
fun Chain.plus(name: String) = chain(this linkTo name)
infix fun Chain.linkTo(name: String) = ChainLink(this, name)
fun chain(atom: Atom, vararg names: String) = chain(atom).fold(names) { plus(it) }

fun NotationLink.plus(literal: Literal): NotationLink =
  notation(this) linkTo line(chain(atom(literal)))

fun NotationLink.plus(name: String): NotationLink =
  null
    ?: runIfNotNull(line.plusOrNull(name)) { lhs linkTo it }
    ?: notation(this) linkTo line(chain(atom(name)))

val useDottedNotation = false

fun NotationLine.plusOrNull(name: String): NotationLine? =
  when (this) {
    is ChainNotationLine -> notNullIf(useDottedNotation) { line(chain.plus(name)) }
    is FieldNotationLine -> null
  }
