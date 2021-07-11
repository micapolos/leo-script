package leo

interface Leo {
  val toScriptLine: ScriptLine
}

abstract class LeoObject: Leo {
  abstract override val toScriptLine: ScriptLine
  final override fun toString() = toScriptLine.toString()
  final override fun equals(other: Any?) = (other is LeoObject) && toScriptLine == other.toScriptLine
  final override fun hashCode() = toScriptLine.hashCode()
}

abstract class LeoEnum: LeoObject() {
  abstract override val toScriptLine: ScriptLine
  abstract val toCaseScriptLine: ScriptLine
}
