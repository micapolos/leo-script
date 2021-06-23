private
  define
    integer.class
    is "java.lang.Integer".class.java.class

  test
    integer.class.quote
    is equal to "class java.lang.Integer".native.java.class

  define
    kt int
    class
    is "leo16.native.IntKt".class.java.class

  test
    int.kt.class.quote
    is equal to "class leo16.native.IntKt".native.java.class

  define
    int.class
    is
      integer.class
      field "TYPE"
      get null.java
      class

  test
    int.class.quote
    is equal to "int".native.java.class

  private
    define
      op text any
      method
      does
        int.kt.class
        method
          op.text
          args
            element int.class.java
            element int.class.java
            array.java

  define
    plus.method
    is "plus".op.method

  test
    plus.method.quote
    is equal to "public static final int leo16.native.IntKt.plus(int,int)".native.java.method

  define
    minus.method
    is "minus".op.method

  test
    minus.method.quote
    is equal to "public static final int leo16.native.IntKt.minus(int,int)".native.java.method

  define
    times.method
    is "times".op.method

  test
    times.method.quote
    is equal to "public static final int leo16.native.IntKt.times(int,int)".native.java.method

define
  number any
  int
  does number.integer.java.int

test
  123.int.quote
  is equal to "123".native.java.int

define
  int any
  plus int any
  does
    quote plus
    method
    invoke
      int.java
      args
        element int.java
        element plus.int.java
        array.java
    int

test
  2.int
  plus 3.int
  is equal to 5.int

define
  int any
  minus int any
  does
    quote minus
    method
    invoke
      int.java
      args
        element int.java
        element minus.int.java
        array.java
    int

test
  5.int
  minus 3.int
  is equal to 2.int

define
  int any
  times int any
  does
    quote times
    method
    invoke
      int.java
      args
        element int.java
        element times.int.java
        array.java
    int

test
  2.int
  times 3.int
  is equal to 6.int
