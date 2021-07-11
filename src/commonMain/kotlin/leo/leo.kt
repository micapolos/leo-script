package leo

abstract class Leo {
  abstract val toScriptLine: ScriptLine
  final override fun toString() = toScriptLine.toString()
  final override fun equals(other: Any?) = (other is Leo) && toScriptLine == other.toScriptLine
  final override fun hashCode() = toScriptLine.hashCode()
}