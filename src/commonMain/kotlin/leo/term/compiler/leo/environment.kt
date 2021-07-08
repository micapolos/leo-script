package leo.term.compiler.leo

import leo.Script
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.term.compiler.Environment
import leo.term.fn
import leo.term.get
import leo.term.head
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.tail
import leo.term.typed.typed
import leo.textTypeLine
import leo.type

val scriptEnvironment: Environment<Script>
  get() =
    Environment(
      { literal -> script(literal).nativeTerm },
      { typedTerm ->
        when (typedTerm.t) {
          type(numberTypeLine, "plus" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                script(
                  "lambda" lineTo script(
                    "lambda" lineTo script(
                      "variable" lineTo script(literal(1)),
                      "plus" lineTo script("variable" lineTo script(literal(0)))
                    )
                  )
                )
                  .nativeTerm.invoke(get<Script>(0).tail).invoke(get<Script>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "minus" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                script(
                  "lambda" lineTo script(
                    "lambda" lineTo script(
                      "variable" lineTo script(literal(1)),
                      "minus" lineTo script("variable" lineTo script(literal(0)))
                    )
                  )
                )
                  .nativeTerm.invoke(get<Script>(0).tail).invoke(get<Script>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(numberTypeLine, "times" lineTo type(numberTypeLine)) ->
            typed(
              fn(
                script(
                  "lambda" lineTo script(
                    "lambda" lineTo script(
                      "variable" lineTo script(literal(1)),
                      "times" lineTo script("variable" lineTo script(literal(0)))
                    )
                  )
                )
                  .nativeTerm.invoke(get<Script>(0).tail).invoke(get<Script>(0).head)
              ).invoke(typedTerm.v),
              type(numberTypeLine)
            )
          type(textTypeLine, "plus" lineTo type(textTypeLine)) ->
            typed(
              fn(
                script(
                  "lambda" lineTo script(
                    "lambda" lineTo script(
                      "variable" lineTo script(literal(1)),
                      "plus" lineTo script("variable" lineTo script(literal(0)))
                    )
                  )
                )
                  .nativeTerm.invoke(get<Script>(0).tail).invoke(get<Script>(0).head)
              ).invoke(typedTerm.v),
              type(textTypeLine)
            )
          else -> null
        }
      }
    )
