package leo.interactive

import leo.Environment
import leo.Evaluator
import leo.Field
import leo.Literal
import leo.Value
import leo.applyEvaluationOrNull
import leo.base.Effect
import leo.base.orIfNull
import leo.base.update
import leo.beName
import leo.begin
import leo.dictionary
import leo.evaluation
import leo.field
import leo.fieldTo
import leo.isEmpty
import leo.plus
import leo.set
import leo.updateValue
import leo.value

typealias Environmental<T> = Effect<Environment, T>
typealias Input = Environmental<Evaluator>
typealias Output = Processor<Environmental<Evaluator>?, Token>
typealias Ret = (Input) -> Output

val Effect<Environment, *>.environment: Environment get() = state
val Effect<*, Evaluator>.evaluator: Evaluator get() = value

val Input.begin: Input get() = update { it.begin }

fun Input.setValue(value: Value): Input = update { it.set(value) }
fun Input.updateValue(fn: (Value) -> Value): Input = update { it.updateValue(fn) }

val Input.tokenizerEvaluation: Output get() =
	tokenizerEvaluation { error("unexpected end") }

fun Input.tokenizerEvaluation(ret: Ret): Output =
  processor { token ->
	  process(token) {
		  ret(it)
	  }
  }

fun Input.process(token: Token, ret: Ret): Output =
	when (token) {
		is BeginToken -> process(token.begin, ret)
		is EndToken -> process(token.end, ret)
		is LiteralToken -> process(token.literal, ret)
	}

fun Input.process(@Suppress("UNUSED_PARAMETER") end: End, ret: Ret): Output =
	ret(this)

fun Input.process(nameBegin: NameBegin, ret: Ret): Output =
	when (nameBegin.name) {
		beName -> processBe(ret)
		else -> processField(nameBegin, ret)
	}

fun Input.process(literal: Literal, ret: Ret): Output =
	process(field(literal), ret)

fun Input.processBe(ret: Ret): Output =
	begin.tokenizerEvaluation { rhs ->
		setValue(rhs.evaluator.value).tokenizerEvaluation(ret)
	}

fun Input.processField(nameBegin: NameBegin, ret: Ret): Output =
	begin.tokenizerEvaluation { rhs ->
		if (rhs.evaluator.value.isEmpty)
			rhs.setValue(value()).process(nameBegin.name fieldTo evaluator.value, ret)
		else
			process(nameBegin.name fieldTo rhs.evaluator.value, ret)
	}

fun Input.process(field: Field, ret: Ret): Output =
	updateValue { it.plus(field) }
		.resolve
		.tokenizerEvaluation(ret)

val Input.resolve: Input get() =
	evaluator
		.dictionary
		.applyEvaluationOrNull(evaluator.value)
		.orIfNull { evaluator.value.resolve.evaluation }
		.run(environment)
		.update { value -> evaluator.set(value) }
