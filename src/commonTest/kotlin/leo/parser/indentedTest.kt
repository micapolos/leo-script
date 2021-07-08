package leo.parser

import leo.base.assertEqualTo
import kotlin.test.Test

class IndentedTest {
  @Test
  fun indented() {
    stringParser.indented.run {
      parsed("").assertEqualTo("")
      parsed(" ").assertEqualTo(null)
      parsed("  ").assertEqualTo(null)
      parsed("  \n").assertEqualTo("\n")
      parsed("  a").assertEqualTo(null)
      parsed("  a\n").assertEqualTo("a\n")
      parsed("  ab").assertEqualTo(null)
      parsed("  ab\n").assertEqualTo("ab\n")
      parsed("  ab\n ").assertEqualTo(null)
      parsed("  ab\n  ").assertEqualTo(null)
      parsed("  ab\n  \n").assertEqualTo("ab\n\n")
      parsed("  ab\n  c\n").assertEqualTo("ab\nc\n")
      parsed("  ab\n  cd\n").assertEqualTo("ab\ncd\n")
    }
  }
}