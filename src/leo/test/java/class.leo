test
  "java.lang.String".class.java
  as java any
  quote
  is equal java native "class java.lang.String"

test
  try "unknown".class.java
  is matching try error any

test
  "Hello, world!".java.object.class
  as class java any
  quote
  is equal class java native "class java.lang.String"
