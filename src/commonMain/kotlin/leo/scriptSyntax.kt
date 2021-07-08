package leo

val Script.syntax get() = syntaxEvaluation.get

val Script.syntaxEvaluation: Evaluation<Syntax>
  get() =
    evaluation.map { syntaxSyntaxing.get }