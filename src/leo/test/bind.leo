test
  data
    "Hello, world!"
    123
    doing foo
    x 10
    y 20
  bind
    a text
    a number
    a doing
    a x
    a y
    a content
  is equal
    a "Hello, world!"
    a 123
    a doing foo
    a x 10
    a y 20
    a
      "Hello, world!"
      123
      doing foo
      x 10
      y 20
