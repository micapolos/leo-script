test
  "java.lang.String".class.java
  as java anything
  quote
  is equal to java native "class java.lang.String"

test
  try "unknown".class.java
  is matching quote try error anything

test
  "Hello, world!".java.object.class
  as class java anything
  quote
  is equal to class java native "class java.lang.String"
