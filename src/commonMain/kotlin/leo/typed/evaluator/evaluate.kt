package leo.typed.evaluator

import leo.CompileError
import leo.Script
import leo.errorName
import leo.lineTo
import leo.script
import leo.typed.compiled.indexed.indexedExpression
import leo.typed.compiler.compiled
import leo.typed.compiler.native.nativeEnvironment
import leo.typed.indexed.native.nativeEvaluator
import leo.typed.indexed.native.script
import leo.typed.indexed.value

val Script.evaluate: Script
  get() =
    try {
      compiled(nativeEnvironment).let { compiled ->
        compiled.indexedExpression.value(nativeEvaluator).script(compiled.type)
      }
    } catch (compileError: CompileError) {
      script(errorName lineTo compileError.scriptFn())
    }
