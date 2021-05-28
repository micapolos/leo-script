private
  let
    integer.class
    be "java.lang.Integer".class.java.class

  test
    integer.class.quote
    is equal "class java.lang.Integer".native.java.class

  let
    kt int
    class
    be "leo16.native.IntKt".class.java.class

  test
    int.kt.class.quote
    is equal "class leo16.native.IntKt".native.java.class

  let
    int.class
    be
      integer.class
      field "TYPE"
      get null.java
      class

  test
    int.class.quote
    is equal "int".native.java.class

  private
    let
      op text any
      method
      do
        int.kt.class
        method
          op.text
          args
            element int.class.java
            element int.class.java
            array.java

  let
    plus.method
    be "plus".op.method

  test
    plus.method.quote
    is equal "public static final int leo16.native.IntKt.plus(int,int)".native.java.method

  let
    minus.method
    be "minus".op.method

  test
    minus.method.quote
    is equal "public static final int leo16.native.IntKt.minus(int,int)".native.java.method

  let
    times.method
    be "times".op.method

  test
    times.method.quote
    is equal "public static final int leo16.native.IntKt.times(int,int)".native.java.method

let
  number any
  int
  do number.integer.java.int

test
  123.int.quote
  is equal "123".native.java.int

let
  int any
  plus int any
  do
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
  is equal 5.int

let
  int any
  minus int any
  do
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
  is equal 2.int

let
  int any
  times int any
  do
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
  is equal 6.int
