package leo

val Notation.script: Script
	get() =
		when (this) {
			EmptyNotation -> script()
			is LinkNotation -> link.script
		}

val NotationLink.script: Script
	get() =
		lhs.script.plus(line)

fun Script.plus(notationLine: NotationLine): Script =
	when (notationLine) {
		is ChainNotationLine -> plus(notationLine.chain)
		is FieldNotationLine -> plus(line(notationLine.field.scriptField))
	}

fun Script.plus(chain: Chain): Script =
	when (chain) {
		is AtomChain -> plus(chain.atom)
		is LinkChain -> plus(chain.link)
	}

fun Script.plus(link: ChainLink): Script =
	plus(link.lhsChain).plus(link.rhsName lineTo script())

fun Script.plus(atom: Atom): Script =
	plus(atom.scriptLine)

val NotationField.scriptField: ScriptField
	get() =
		name fieldTo notationLink.script

val Atom.scriptLine: ScriptLine
	get() =
		when (this) {
			is LiteralAtom -> line(literal)
			is NameAtom -> line(name fieldTo script())
		}
