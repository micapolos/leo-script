package leo

import leo.base.effect

typealias Evaluation<V> = Stateful<Environment, V>

val <T> Evaluation<T>.get: T get() = get(environment())
fun <V> evaluation(value: V): Evaluation<V> = value.stateful()
val <V> V.evaluation get() = evaluation(this)

fun <T> Evaluation<T>.tracing(value: Value): Evaluation<T> =
  Evaluation { environment ->
    environment.trace.let { trace ->
      run(environment.copy(trace = trace.push(value))).let { effect ->
        effect.state.copy(trace = trace) effect effect.value
      }
    }
  }

val traceEvaluation: Evaluation<Trace>
  get() =
    Evaluation { it effect it.trace }

fun <T> Value.failEvaluation(): Evaluation<T> =
  traceEvaluation.bind { trace ->
    plus(trace.value).throwError()
  }

val Value.contentEvaluation: Evaluation<Value>
  get() =
    fieldOrNull?.valueOrNull?.evaluation
      ?: plus(contentName).throwError()

val Value.contentOrNull: Value?
  get() =
    fieldOrNull?.valueOrNull
