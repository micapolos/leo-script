package leo.base.stak

import leo.base.assertContains
import kotlin.test.Test

class UtilTest {
  @Test
  fun seq() {
    stakOf(1, 2, 3).seq.assertContains(3, 2, 1)
  }
}