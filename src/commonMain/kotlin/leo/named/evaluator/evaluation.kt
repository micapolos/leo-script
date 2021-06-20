package leo.named.evaluator

import leo.Literal
import leo.Stateful
import leo.Type
import leo.bind
import leo.foldStateful
import leo.map
import leo.named.expression.AnyLine
import leo.named.expression.Be
import leo.named.expression.BeLetRhs
import leo.named.expression.BeLine
import leo.named.expression.Bind
import leo.named.expression.BindLine
import leo.named.expression.Body
import leo.named.expression.Do
import leo.named.expression.DoLetRhs
import leo.named.expression.DoLine
import leo.named.expression.Doing
import leo.named.expression.DoingLine
import leo.named.expression.Expression
import leo.named.expression.ExpressionBody
import leo.named.expression.Field
import leo.named.expression.FieldLine
import leo.named.expression.FnBody
import leo.named.expression.Get
import leo.named.expression.GetLine
import leo.named.expression.Give
import leo.named.expression.GiveLine
import leo.named.expression.Invoke
import leo.named.expression.InvokeLine
import leo.named.expression.Let
import leo.named.expression.LetLine
import leo.named.expression.LetRhs
import leo.named.expression.Line
import leo.named.expression.LiteralLine
import leo.named.expression.Make
import leo.named.expression.MakeLine
import leo.named.expression.Private
import leo.named.expression.PrivateLine
import leo.named.expression.Recursive
import leo.named.expression.RecursiveLine
import leo.named.expression.Switch
import leo.named.expression.SwitchLine
import leo.named.expression.With
import leo.named.expression.WithLine
import leo.named.expression.expression
import leo.named.expression.lineSeq
import leo.named.value.Value
import leo.named.value.ValueFunction
import leo.named.value.anyValueLine
import leo.named.value.function
import leo.named.value.get
import leo.named.value.giveEvaluation
import leo.named.value.line
import leo.named.value.lineTo
import leo.named.value.make
import leo.named.value.name
import leo.named.value.plus
import leo.named.value.unsafeSwitchLine
import leo.named.value.with
import leo.stateful

typealias Evaluation<V> = Stateful<Unit, V>
fun <V> V.evaluation(): Evaluation<V> = stateful()

fun Dictionary.valueEvaluation(body: Body): Evaluation<Value> =
	when (body) {
		is ExpressionBody -> valueEvaluation(body.expression)
		is FnBody -> body.valueFn(this).evaluation()
	}

fun Dictionary.dictionaryEvaluation(expression: Expression): Evaluation<Dictionary> =
	private.module.evaluator.plusEvaluation(expression).map { it.module.public.dictionary }

fun Dictionary.moduleEvaluation(expression: Expression): Evaluation<Module> =
	private.module.evaluator.plusEvaluation(expression).map { it.module }

fun Dictionary.valueEvaluation(expression: Expression): Evaluation<Value> =
	private.module.evaluator.plusEvaluation(expression).map { it.value }

fun Evaluator.plusEvaluation(expression: Expression): Evaluation<Evaluator> =
	evaluation().foldStateful(expression.lineSeq) { plusEvaluation(it) }

fun Evaluator.plusEvaluation(line: Line): Evaluation<Evaluator> =
	when (line) {
		is AnyLine -> plusAnyEvaluation(line.any)
		is BeLine -> plusEvaluation(line.be)
		is BindLine -> plusEvaluation(line.bind)
		is DoLine -> plusEvaluation(line.do_)
		is DoingLine -> plusEvaluation(line.doing)
		is FieldLine -> plusEvaluation(line.field)
		is GetLine -> plusEvaluation(line.get)
		is GiveLine -> plusEvaluation(line.give)
		is InvokeLine -> plusEvaluation(line.invoke)
		is LetLine -> plusEvaluation(line.let)
		is LiteralLine -> plusEvaluation(line.literal)
		is MakeLine -> plusEvaluation(line.make)
		is SwitchLine -> plusEvaluation(line.switch)
		is WithLine -> plusEvaluation(line.with)
		is RecursiveLine -> plusEvaluation(line.recursive)
		is PrivateLine -> plusEvaluation(line.private)
	}

fun Evaluator.plusAnyEvaluation(any: Any?): Evaluation<Evaluator> =
	set(value.plus(any.anyValueLine)).evaluation()

fun Evaluator.plusEvaluation(be: Be): Evaluation<Evaluator> =
	module.private.dictionary.valueEvaluation(be.expression).map { set(it) }

fun Evaluator.plusEvaluation(bind: Bind): Evaluation<Evaluator> =
	module.private.dictionary.valueEvaluation(bind.expression).map { value ->
		set(module.bind(value))
	}

fun Evaluator.plusEvaluation(do_: Do): Evaluation<Evaluator> =
	module.private.dictionary.plus(value.givenDictionary).valueEvaluation(do_.body).map { set(it) }

fun Evaluator.plusEvaluation(with: With): Evaluation<Evaluator> =
	dictionary.valueEvaluation(with.expression).map {
		set(value.with(it))
	}

fun Evaluator.plusEvaluation(recursive: Recursive): Evaluation<Evaluator> =
	dictionary.dictionaryEvaluation(recursive.expression).map {
		set(module.plusRecursive(it))
	}

fun Evaluator.plusEvaluation(private: Private): Evaluation<Evaluator> =
	dictionary.dictionaryEvaluation(private.expression).map {
		set(module.plusPrivate(it))
	}

fun Evaluator.plusEvaluation(field: Field): Evaluation<Evaluator> =
	module.private.dictionary.valueEvaluation(field.expression).map {
		set(value.plus(field.name lineTo it))
	}

fun Evaluator.plusEvaluation(literal: Literal): Evaluation<Evaluator> =
	set(value.plus(literal.any.anyValueLine)).evaluation()

fun Evaluator.plusEvaluation(make: Make): Evaluation<Evaluator> =
	set(value.make(make.name)).evaluation()

fun Evaluator.plusEvaluation(doing: Doing): Evaluation<Evaluator> =
	set(value.plus(line(function(dictionary, doing.body)))).evaluation()

fun Evaluator.plusEvaluation(get: Get): Evaluation<Evaluator> =
	set(value.get(get.name)).evaluation()

fun Evaluator.plusEvaluation(give: Give): Evaluation<Evaluator> =
	dictionary.valueEvaluation(give.expression).bind { given ->
		value.giveEvaluation(given).map { set(it) }
	}

fun Evaluator.plusEvaluation(let: Let): Evaluation<Evaluator> =
	dictionary.bindingEvaluation(let.rhs).map {
		set(module.plus(definition(let.type, it)))
	}

fun Dictionary.bindingEvaluation(letRhs: LetRhs): Evaluation<Binding> =
	when(letRhs) {
		is BeLetRhs -> bindingEvaluation(letRhs.be)
		is DoLetRhs -> bindingEvaluation(letRhs.do_)
	}

fun Dictionary.bindingEvaluation(be: Be): Evaluation<Binding> =
	valueEvaluation(be.expression).map { binding(it) }

fun Dictionary.bindingEvaluation(do_: Do): Evaluation<Binding> =
	functionEvaluation(do_).map { binding(it) }

fun Dictionary.functionEvaluation(do_: Do): Evaluation<ValueFunction> =
	function(this, do_.body).evaluation()

fun Evaluator.plusEvaluation(invoke: Invoke): Evaluation<Evaluator> =
	dictionary.giveEvaluation(invoke.type, value).map { set(it) }

fun Evaluator.plusEvaluation(switch: Switch): Evaluation<Evaluator> =
	value.unsafeSwitchLine.let { valueLine ->
		module.private.dictionary
			.plus(valueLine.definition)
			.valueEvaluation(switch.expression(valueLine.name))
			.map { set(it) }
	}

fun Dictionary.giveEvaluation(type: Type, value: Value): Evaluation<Value> =
	binding(type).giveEvaluation(value)

fun Binding.giveEvaluation(given: Value): Evaluation<Value> =
	when (this) {
		is FunctionBinding -> function.invokeEvaluation(given)
		is RecursiveBinding -> error("$this.apply($given)")
		is ValueBinding -> value.evaluation()
	}

fun ValueFunction.invokeEvaluation(value: Value): Evaluation<Value> =
	dictionary.plus(value.givenDictionary).valueEvaluation(body)
