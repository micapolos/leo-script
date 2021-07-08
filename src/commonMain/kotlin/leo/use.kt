package leo

import leo.natives.fileText
import leo.parser.scriptEvaluation

@kotlin.jvm.JvmInline
value class Use(val nameStackLink: StackLink<String>)

fun use(name: String, vararg names: String) = Use(stackLink(name, *names))

val Script.useOrNull: Use?
  get() =
    nameStackOrNull?.reverse?.linkOrNull?.let { nameStackLink ->
      Use(nameStackLink)
    }

val Use.fileNameString: String
  get() =
    stack(nameStackLink).array.joinToString("/") + ".leo"

val String.fileNameStringEvaluation: Evaluation<String>
  get() =
    evaluation
      .map { it.fileText }
      //.tracing(value("load" fieldTo value(field(literal(this)))))
      .catch { value(field(literal(it.message ?: it.toString()))).throwError() }

val Use.stringEvaluation: Evaluation<String>
  get() =
    fileNameString.fileNameStringEvaluation

val Use.dictionaryEvaluation: Evaluation<Dictionary>
  get() =
    stringEvaluation
      .bind { it.scriptEvaluation }
      .bind { it.syntaxEvaluation }
      .bind { it.dictionaryEvaluation }
