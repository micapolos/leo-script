package leo.named.expression

import leo.Literal
import leo.Stack
import leo.Type
import leo.base.notNullIf
import leo.base.notNullOrError
import leo.isName
import leo.literal
import leo.mapFirst
import leo.named.evaluator.Dictionary
import leo.named.value.Value
import leo.noName
import leo.push
import leo.reverse
import leo.seq
import leo.stack
import leo.yesName

data class Expression(val lineStack: Stack<Line>) { override fun toString() = scriptLine.toString() }

sealed class Line { override fun toString() = scriptLine.toString() }
data class AnyLine(val any: Any?): Line() { override fun toString() = super.toString() }
data class BeLine(val be: Be): Line() { override fun toString() = super.toString() }
data class BindLine(val bind: Bind): Line() { override fun toString() = super.toString() }
data class DoLine(val do_: Do): Line() { override fun toString() = super.toString() }
data class FunctionLine(val function: Function): Line() { override fun toString() = super.toString() }
data class FieldLine(val field: Field): Line() { override fun toString() = super.toString() }
data class GetLine(val get: Get): Line() { override fun toString() = super.toString() }
data class GiveLine(val give: Give): Line() { override fun toString() = super.toString() }
data class InvokeLine(val invoke: Invoke): Line() { override fun toString() = super.toString() }
data class EqualsLine(val equals: Equals): Line() { override fun toString() = super.toString() }
data class LiteralLine(val literal: Literal): Line() { override fun toString() = super.toString() }
data class LetLine(val let: Let): Line() { override fun toString() = super.toString() }
data class MakeLine(val make: Make): Line() { override fun toString() = super.toString() }
data class PrivateLine(val private: Private): Line() { override fun toString() = super.toString() }
data class RecursiveLine(val recursive: Recursive): Line() { override fun toString() = super.toString() }
data class SwitchLine(val switch: Switch): Line() { override fun toString() = super.toString() }
data class TakeLine(val take: Take): Line() { override fun toString() = super.toString() }
data class WithLine(val with: With): Line() { override fun toString() = super.toString() }

data class Be(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Bind(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Case(val name: String, val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Do(val doing: Doing) { override fun toString() = scriptLine.toString() }
data class Field(val name: String, val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Function(val type: Type, val doing: Doing) { override fun toString() = scriptLine.toString() }
data class Get(val name: String) { override fun toString() = scriptLine.toString() }
data class Give(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Invoke(val type: Type) { override fun toString() = scriptLine.toString() }
data class Equals(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Let(val type: Type, val rhs: LetRhs) { override fun toString() = scriptLine.toString() }
data class Make(val name: String) { override fun toString() = scriptLine.toString() }
data class Private(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Recursive(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class Switch(val cases: Stack<Case>) { override fun toString() = scriptLine.toString() }
data class Take(val expression: Expression) { override fun toString() = scriptLine.toString() }
data class With(val expression: Expression) { override fun toString() = scriptLine.toString() }

sealed class LetRhs { override fun toString() = scriptLine.toString() }
data class BeLetRhs(val be: Be): LetRhs() { override fun toString() = super.toString() }
data class DoLetRhs(val do_: Do): LetRhs() { override fun toString() = super.toString() }

sealed class Doing { override fun toString() = script.toString() }
data class ExpressionDoing(val expression: Expression): Doing() { override fun toString() = super.toString() }
data class FnDoing(val valueFn: (Dictionary) -> Value): Doing() { override fun toString() = super.toString() }

fun expressionLine(literal: Literal): Line = LiteralLine(literal)
fun anyExpressionLine(any: Any?): Line = AnyLine(any)
fun line(be: Be): Line = BeLine(be)
fun line(bind: Bind): Line = BindLine(bind)
fun line(do_: Do): Line = DoLine(do_)
fun line(equals: Equals): Line = EqualsLine(equals)
fun line(field: Field): Line = FieldLine(field)
fun line(function: Function): Line = FunctionLine(function)
fun line(get: Get): Line = GetLine(get)
fun line(give: Give): Line = GiveLine(give)
fun line(let: Let): Line = LetLine(let)
fun line(make: Make): Line = MakeLine(make)
fun line(invoke: Invoke): Line = InvokeLine(invoke)
fun line(private: Private): Line = PrivateLine(private)
fun line(recursive: Recursive): Line = RecursiveLine(recursive)
fun line(switch: Switch): Line = SwitchLine(switch)
fun line(take: Take): Line = TakeLine(take)
fun line(with: With): Line = WithLine(with)

val Stack<Line>.expression get() = Expression(this)
fun Expression.plus(line: Line) = lineStack.push(line).expression
fun expression(vararg lines: Line): Expression = Expression(stack(*lines))
fun expression(name: String): Expression = expression(name lineTo expression())

infix fun String.fieldTo(rhs: Expression) = Field(this, rhs)
infix fun String.lineTo(rhs: Expression) = line(this fieldTo rhs)
infix fun String.caseTo(expression: Expression) = Case(this, expression)

fun doing(expression: Expression): Doing = ExpressionDoing(expression)
fun doing(fn: Dictionary.() -> Value): Doing = FnDoing(fn)

fun be(expression: Expression) = Be(expression)
fun bind(expression: Expression) = Bind(expression)
fun do_(doing: Doing) = Do(doing)
fun equals_(expression: Expression) = Equals(expression)
fun function(type: Type, doing: Doing) = Function(type, doing)
fun get(name: String) = Get(name)
fun give(expression: Expression) = Give(expression)
fun invoke(type: Type) = Invoke(type)
fun let(type: Type, rhs: LetRhs) = Let(type, rhs)
fun make(name: String) = Make(name)
fun private(expression: Expression) = Private(expression)
fun recursive(expression: Expression) = Recursive(expression)
fun switch(cases: Stack<Case>) = Switch(cases)
fun switch(vararg cases: Case) = Switch(stack(*cases))
fun take(expression: Expression) = Take(expression)
fun with(rhs: Expression) = With(rhs)

fun rhs(be: Be): LetRhs = BeLetRhs(be)
fun rhs(do_: Do): LetRhs = DoLetRhs(do_)

fun function(type: Type, expression: Expression) = function(type, doing(expression))
fun function(type: Type, fn: Dictionary.() -> Value) = function(type, doing(fn))

fun Switch.expression(name: String): Expression = expressionOrNull(name).notNullOrError("$this.expression($name)")
fun Switch.expressionOrNull(name: String): Expression? = cases.mapFirst { expressionOrNull(name) }
fun Case.expressionOrNull(name: String): Expression? = notNullIf(this.name == name) { expression }

val Expression.lineSeq get() = lineStack.reverse.seq

fun Expression.give(expression: Expression): Expression = plus(line(leo.named.expression.give(expression)))
fun Expression.take(expression: Expression): Expression = plus(line(leo.named.expression.take(expression)))
fun Expression.be(expression: Expression): Expression = plus(line(leo.named.expression.be(expression)))
fun Expression.bind(expression: Expression): Expression = plus(line(leo.named.expression.bind(expression)))
fun Expression.make(name: String): Expression = plus(line(leo.named.expression.make(name)))
fun Expression.invoke(type: Type): Expression = plus(line(leo.named.expression.invoke(type)))
fun Expression.isEqualTo(expression: Expression): Expression = plus(line(equals_(expression)))
val Expression.negate: Expression get() = plus(line(switch(
	yesName caseTo isYesExpression,
	noName caseTo isNoExpression)))
infix fun Type.lineTo(doing: Doing): Line = line(function(this, doing))

fun Expression.get(name: String): Expression = plus(line(leo.named.expression.get(name)))
fun Expression.switch(caseStack: Stack<Case>) = plus(line(Switch(caseStack)))

val Int.numberExpressionLine get() = expressionLine(literal(this))
val String.textExpressionLine get() = expressionLine(literal(this))
val Boolean.isExpressionLine get() = isName lineTo expression(if (this) yesName else noName)

val Int.numberExpression get() = expression(numberExpressionLine)
val String.textExpression get() = expression(textExpressionLine)
val Boolean.isExpression get() = expression(isExpressionLine)
val isYesExpression get() = true.isExpression
val isNoExpression get() = false.isExpression