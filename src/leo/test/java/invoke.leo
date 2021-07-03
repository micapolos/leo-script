test
  "java.lang.String".class.java.class
  method
    "concat"
    args
      element "java.lang.String".class.java
      array.java
  invoke
    "Hello, ".java
    args
      element "world!".java
      array.java
  as java anything
  quote
  is equal to java native "Hello, world!"
