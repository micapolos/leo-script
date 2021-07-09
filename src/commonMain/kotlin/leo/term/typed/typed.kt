package leo.term.typed

import leo.ChoiceType
import leo.Literal
import leo.StructureType
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.atomOrNull
import leo.base.Seq
import leo.base.filterMap
import leo.base.fold
import leo.base.notNullIf
import leo.base.notNullOrError
import leo.base.onlyOrNull
import leo.base.orIfNull
import leo.base.orNullIf
import leo.base.seq
import leo.base.the
import leo.base.then
import leo.choice
import leo.choiceOrNull
import leo.empty
import leo.fieldOrNull
import leo.fold
import leo.functionLineTo
import leo.functionOrNull
import leo.isEmpty
import leo.isStatic
import leo.lineTo
import leo.linkOrNull
import leo.name
import leo.named.evaluator.any
import leo.onlyLineOrNull
import leo.plus
import leo.script
import leo.scriptLine
import leo.stack
import leo.structure
import leo.structureOrNull
import leo.term.Term
import leo.term.Value
import leo.term.anyTerm
import leo.term.compiler.Get
import leo.term.compiler.compileError
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.fix
import leo.term.fn
import leo.term.head
import leo.term.invoke
import leo.term.plus
import leo.term.tail
import leo.term.term
import leo.type
import leo.typeLine

data class Typed<out V, out T>(val v: V, val t: T)

fun <V, T> typed(v: V, t: T) = Typed(v, t)

typealias TypedValue<V> = Typed<Value<V>, Type>
typealias TypedTerm<V> = Typed<Term<V>, Type>
typealias TypedLine<V> = Typed<Term<V>, TypeLine>
typealias TypedChoice<V> = Typed<Term<V>?, TypeChoice>

sealed class TypedSelection<out V>
data class YesTypedSelection<V>(val typedLine: TypedLine<V>) : TypedSelection<V>()
data class NoTypedSelection<V>(val typeLine: TypeLine) : TypedSelection<V>()

data class TypedSwitch<V>(val typedTerm: TypedTerm<V>?, val choice: TypeChoice)

fun <V> noSelection(typeLine: TypeLine): TypedSelection<V> = NoTypedSelection(typeLine)
fun <V> yesSelection(typedLine: TypedLine<V>): TypedSelection<V> = YesTypedSelection(typedLine)

fun <V> typedTerm(): TypedTerm<V> = Typed(term(empty), type())

fun <V> typedChoice(): TypedChoice<V> = Typed(null, choice())

fun <V> TypedTerm<V>.plus(line: TypedLine<V>): TypedTerm<V> =
  typed(
    if (t.isStatic)
      if (line.t.isStatic) term(empty)
      else line.v
    else
      if (line.t.isStatic) v
      else v.plus(line.v),
    t.plus(line.t)
  )

infix fun <V> String.lineTo(typed: TypedTerm<V>): TypedLine<V> =
  typed(typed.v, this lineTo typed.t)

fun <V> typedTerm(vararg lines: TypedLine<V>): TypedTerm<V> =
  typedTerm<V>().fold(lines) { plus(it) }

fun <V> typedTerm(name: String): TypedTerm<V> =
  typedTerm(name lineTo typedTerm())

fun typedLine(literal: Literal): TypedLine<Any?> =
  typed(literal.any.anyTerm, literal.typeLine)

val <V> TypedTerm<V>.pairOrNull: Pair<TypedTerm<V>, TypedLine<V>>?
  get() =
    t.structureOrNull?.lineStack?.linkOrNull?.let { link ->
      link.tail.structure.type.let { type ->
        link.head.let { line ->
          if (type.isStatic)
            if (line.isStatic) typed(term<V>(empty), type) to typed(term(empty), line)
            else typed(term<V>(empty), type) to typed(v, line)
          else
            if (line.isStatic) typed(v, type) to typed(term(empty), line)
            else typed(v.tail, type) to typed(v.head, line)
        }
      }
    }

val <V> TypedTerm<V>.headOrNull: TypedTerm<V>?
  get() =
    pairOrNull?.second?.let { typedTerm(it) }

val <V> TypedTerm<V>.tailOrNull: TypedTerm<V>?
  get() =
    pairOrNull?.first

val <V> TypedTerm<V>.onlyLineOrNull: TypedLine<V>?
  get() =
    pairOrNull?.let { (lhs, rhs) ->
      notNullIf(lhs.t.isEmpty) { rhs }
    }

val <V> TypedTerm<V>.content: TypedTerm<V>
  get() =
    when (t) {
      is ChoiceType -> null
      is StructureType -> onlyLineOrNull?.lineContentOrNull
    } ?: this

val <V> TypedLine<V>.lineContentOrNull: TypedTerm<V>?
  get() =
    rhsOrNull?.content

val <V> TypedLine<V>.rhsOrNull: TypedTerm<V>?
  get() =
    t.atomOrNull?.fieldOrNull?.rhsType?.let { rhs ->
      typed(v, rhs)
    }

fun <V> TypedTerm<V>.invoke(typedTerm: TypedTerm<V>): TypedTerm<V> =
  t.functionOrNull.orIfNull {
    compileError(
      typedTerm.t.script
        .plus("apply" lineTo t.script)
        .plus(
          "is" lineTo script(
            "not" lineTo script(
              "matching" lineTo script(
                "any" lineTo script("expression"),
                "apply" lineTo script("any" lineTo script("function")))))))
  }.let { typeFunction ->
    if (typedTerm.t != typeFunction.lhsType)
      compileError(
        typedTerm.t.script
          .plus("apply" lineTo t.script)
          .plus("is" lineTo script(
            "not" lineTo script(
              "matching" lineTo typeFunction.lhsType.script
                .plus("apply" lineTo script(typeFunction.scriptLine))))))
    else typed(v.invoke(typedTerm.v), typeFunction.rhsType)
  }

fun <V> TypedTerm<V>.get(name: String): TypedTerm<V> =
  getOrNull(name).notNullOrError("$this get $name")

fun <V> TypedTerm<V>.invoke(get: Get): TypedTerm<V> =
  fold(stack(get.nameStackLink)) { get(it) }

fun <V> TypedTerm<V>.getOrNull(name: String): TypedTerm<V>? =
  null
    ?: getDirectNull(name)
    ?: getIndirectNull(name)

fun <V> TypedTerm<V>.getDirectNull(name: String): TypedTerm<V>? =
  lineSeq(name).onlyOrNull?.let { typedTerm(it) }

fun <V> TypedTerm<V>.getIndirectNull(name: String): TypedTerm<V>? =
  indirectLineSeq(name).onlyOrNull?.let { typedTerm(it) }

fun <V> TypedTerm<V>.lineOrNull(name: String): TypedLine<V>? =
  pairOrNull?.let { (typedTerm, typedLine) ->
    null
      ?: typedLine.orNull(name)
      ?: typedTerm.lineOrNull(name)
  }

val <V> TypedTerm<V>.lineSeq: Seq<TypedLine<V>>
  get() =
    seq {
      pairOrNull?.let { (lhs, line) ->
        line.then(lhs.lineSeq)
      }
    }

fun <V> TypedTerm<V>.lineSeq(name: String): Seq<TypedLine<V>> =
  lineSeq.filterMap { orNull(name)?.the }

fun <V> TypedTerm<V>.indirectLineSeq(name: String): Seq<TypedLine<V>> =
  lineSeq.filterMap { indirectOrNull(name)?.the }

fun <V> TypedLine<V>.orNull(name: String): TypedLine<V>? =
  notNullIf(t.name == name) { this }

fun <V> TypedTerm<V>.indirectLineOrNull(name: String): TypedLine<V>? =
  indirectLineSeq(name).onlyOrNull

fun <V> TypedLine<V>.indirectOrNull(name: String): TypedLine<V>? =
  orNull(name) ?: rhsOrNull?.indirectLineOrNull(name)

fun <V> TypedTerm<V>.make(name: String): TypedTerm<V> =
  typedTerm(name lineTo this)

fun <V> TypedTerm<V>.do_(typedTerm: TypedTerm<V>): TypedTerm<V> =
  typed(fn(typedTerm.v).invoke(v), typedTerm.t)

fun <V> TypedTerm<V>.repeat(typedTerm: TypedTerm<V>): TypedTerm<V> =
  typed(fix<V>().invoke(fn(fn(typedTerm.v))).invoke(v), typedTerm.t)

fun <V> typedFunctionLine(type: Type, typedTerm: TypedTerm<V>): TypedLine<V> =
  typed(fn(typedTerm.v), type functionLineTo typedTerm.t)

val <V> TypedSelection<V>.typeLine: TypeLine
  get() =
    when (this) {
      is NoTypedSelection -> typeLine
      is YesTypedSelection -> typedLine.t
    }

fun <V> TypedChoice<V>.choicePlus(selection: TypedSelection<V>): TypedChoice<V> =
  Typed(
    when (selection) {
      is YesTypedSelection ->
        if (v != null) compileError(script("select" lineTo script(
          "duplicate" lineTo script("the" lineTo script(selection.typedLine.t.scriptLine)))))
        else if (t.lineStack.isEmpty) selection.typedLine.v
        else selection.typedLine.v.eitherSecond
      is NoTypedSelection -> v?.eitherFirst
    },
    t.plus(selection.typeLine)
  )

val <V> TypedChoice<V>.typedTerm: TypedTerm<V>
  get() =
    Typed(
      v ?: compileError(script("select" lineTo script("none" lineTo script("selected")))),
      type(t))

fun <V> typedSwitch(choice: TypeChoice) = TypedSwitch<V>(null, choice)

val Type.pickDropChoiceOrNull: TypeChoice? get() =
  choiceOrNull ?: structureOrNull?.orNullIf { !isEmpty }?.let { choice() }

fun <V> TypedTerm<V>.pick(typedTerm: TypedTerm<V>): TypedTerm<V> =
  t.pickDropChoiceOrNull
    .orIfNull {
      compileError(
        t.script
          .plus("pick" lineTo script(typedTerm.t.scriptLine))
          .plus("is" lineTo script(
            "not" lineTo script(
              "matching" lineTo script(
                "any" lineTo script("choice"),
                "pick" lineTo script("any" lineTo script("expression")))))))
    }
    .let { choice ->
      typedTerm.onlyLineOrNull.orIfNull {
        compileError(
          t.script
            .plus("pick" lineTo script(typedTerm.t.scriptLine))
            .plus("is" lineTo script(
              "not" lineTo script(
                "matching" lineTo script(
                  "any" lineTo script("choice"),
                  "pick" lineTo script("any" lineTo script("expression")))))))
      }.let { typedLine ->
        typed(
          if (choice.lineStack.isEmpty) typedLine.v
          else typedLine.v.eitherSecond,
          type(choice.plus(typedLine.t)))
      }
    }

fun <V> TypedTerm<V>.drop(type: Type): TypedTerm<V> =
  t.pickDropChoiceOrNull
    .orIfNull {
      compileError(
        t.script
          .plus("pick" lineTo type.script)
          .plus("is" lineTo script(
            "not" lineTo script(
              "matching" lineTo script(
                "any" lineTo script("choice"),
                "pick" lineTo script("any" lineTo script("type")))))))
    }
    .let { choice ->
      type.onlyLineOrNull.orIfNull {
        compileError(
          t.script
            .plus("pick" lineTo type.script)
            .plus("is" lineTo script(
              "not" lineTo script(
                "matching" lineTo script(
                  "any" lineTo script("choice"),
                  "pick" lineTo script("any" lineTo script("type")))))))
      }.let { typeLine ->
        typed(
          if (choice.lineStack.isEmpty) term(empty)
          else v.eitherFirst,
          type(choice.plus(typeLine)))
      }
    }
