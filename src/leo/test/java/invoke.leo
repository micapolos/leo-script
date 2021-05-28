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
  as java any
  quote
  is equal java native "Hello, world!"
