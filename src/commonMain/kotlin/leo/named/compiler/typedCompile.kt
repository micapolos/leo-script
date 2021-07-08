package leo.named.compiler

import leo.Type
import leo.TypeStructure
import leo.atom
import leo.fieldOrNull
import leo.getOrNull
import leo.isType
import leo.line
import leo.linkOrNull
import leo.make
import leo.named.expression.be
import leo.named.expression.bind
import leo.named.expression.do_
import leo.named.expression.expression
import leo.named.expression.get
import leo.named.expression.isEqualTo
import leo.named.expression.line
import leo.named.expression.make
import leo.named.expression.negate
import leo.named.expression.plus
import leo.named.expression.rhs
import leo.named.typed.TypedChoice
import leo.named.typed.TypedExpression
import leo.named.typed.TypedField
import leo.named.typed.TypedFunction
import leo.named.typed.TypedLine
import leo.named.typed.of
import leo.named.typed.typed
import leo.structure
import leo.structureOrNull
import leo.type

val TypedExpression.resolve: TypedExpression
  get() = this

val TypedExpression.compileOnlyLine: TypedLine
  get() =
    type.compileLine.let { typeLine ->
      typed(expression.line, typeLine)
    }

fun <R> Type.resolveInfix(fn: (Type, String, Type) -> R?): R? =
  structureOrNull?.resolveInfix(fn)

fun <R> TypeStructure.resolveInfix(fn: (Type, String, Type) -> R?): R? =
  lineStack.linkOrNull?.let { link ->
    link.head.atom.fieldOrNull?.let { field ->
      fn(link.tail.structure.type, field.name, field.rhsType)
    }
  }

fun TypedExpression.getOrNull(name: String): TypedExpression? =
  type.getOrNull(name)?.let {
    expression.get(name).of(it)
  }

fun TypedExpression.make(name: String): TypedExpression =
  expression.make(name).of(type.make(name))

fun TypedExpression.of(type: Type): TypedExpression =
  this.type.checkOf(type).let { expression of it }

val TypedExpression.choice: TypedChoice
  get() =
    type.selectChoice.let { typed(expression.choiceLine, it) }

fun TypedExpression.be(typedExpression: TypedExpression): TypedExpression =
  expression.plus(line(be(typedExpression.expression))).of(typedExpression.type)

fun TypedExpression.bind(typedExpression: TypedExpression): TypedExpression =
  expression.bind(typedExpression.expression).of(type)

fun TypedExpression.bind(typedLine: TypedLine): TypedExpression =
  expression.plus(line(bind(expression(typedLine.line)))).of(type)

fun TypedExpression.isEqualTo(typed: TypedExpression): TypedExpression =
  type.check(typed.type) {
    expression.isEqualTo(typed.expression) of isType
  }

val TypedExpression.negate: TypedExpression
  get() =
    type.check(isType) {
      expression.negate of isType
    }

fun TypedExpression.define(typed: TypedField): TypedExpression =
  bind(typed(expression(line(typed.field)), type(typed.typeField.atom.line)))

fun TypedExpression.define(typed: TypedFunction): TypedExpression =
  typed(
    expression
      .plus(line(leo.named.expression.let(typed.typeFunction.lhsType, rhs(do_(typed.function.doing))))),
    type
  )