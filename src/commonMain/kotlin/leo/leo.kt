package leo

interface Leo {
  val leoScriptLine: ScriptLine
}

abstract class LeoObject: Leo {
  abstract override val leoScriptLine: ScriptLine
  final override fun toString() = leoScriptLine.toString()
  final override fun equals(other: Any?) = (other is LeoObject) && leoScriptLine == other.leoScriptLine
  final override fun hashCode() = leoScriptLine.hashCode()
}

abstract class LeoEnum: LeoObject() {
  abstract override val leoScriptLine: ScriptLine
  abstract val leoCaseScriptLine: ScriptLine
}
