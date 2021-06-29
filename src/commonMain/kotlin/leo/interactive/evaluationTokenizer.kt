package leo.interactive

import leo.Evaluation
import leo.Evaluator
import leo.Script
import leo.Value
import leo.beName
import leo.begin
import leo.bind
import leo.context
import leo.environment
import leo.evaluation
import leo.evaluator
import leo.field
import leo.fieldTo
import leo.giveName
import leo.invokeEvaluation
import leo.map
import leo.plusResolveEvaluation
import leo.plusResolveValueEvaluation
import leo.prelude.preludeDictionary
import leo.script
import leo.setEvaluation
import leo.value

fun Evaluation<Evaluator>.tokenizer(ret: (Evaluation<Evaluator>) -> Tokenizer<Evaluation<Evaluator>?>): Tokenizer<Evaluation<Evaluator>?> =
	processor { token ->
		when (token) {
			is BeginToken -> when (token.begin.name) {
				beName -> beginValueTokenizer(ret) { it.evaluation }
				giveName -> beginValueTokenizer(ret) { value.invokeEvaluation(it) }
				else -> beginValueTokenizer(ret) { plusResolveValueEvaluation(token.begin.name fieldTo it) }
			}
			is LiteralToken -> bind { it.plusResolveEvaluation(field(token.literal)) }.tokenizer(ret)
			is EndToken -> ret(this)
		}
	}

fun Evaluation<Evaluator>.beginValueTokenizer(
	ret: (Evaluation<Evaluator>) -> Tokenizer<Evaluation<Evaluator>?>,
	fn: Evaluator.(Value) -> Evaluation<Value>): Tokenizer<Evaluation<Evaluator>?> =
	map { it.begin }.tokenizer { rhsEvaluation ->
		rhsEvaluation.bind { rhsEvaluator ->
			bind {
				it.fn(rhsEvaluator.value).bind { value ->
					it.setEvaluation(value)
				}
			}
		}.tokenizer(ret)
	}

val Evaluation<Evaluator>.tokenizer: Tokenizer<Evaluation<Evaluator>?> get() =
	tokenizer { error("unexpected end") }

val evaluationTokenizer: Tokenizer<Evaluation<Evaluator>?> get() =
	preludeDictionary.context.evaluator(value()).evaluation.tokenizer

val Script.evaluate: Script get() =
	evaluationTokenizer.process(this).state!!.run(environment()).value.value.script