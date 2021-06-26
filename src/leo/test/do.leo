comment
  do
    binds
      all fields of the current value
      by their name
      locally
    and
      does the given script

test
  x 10
  y 20
  do
    x.number
    plus y.number
  is equal to 30

test
  "Hello, world!"
  123
  doing foo
  x 10
  y 20
  do
    my text
    my number
    my doing
    my x
    my y
    my content
  is equal to
    my "Hello, world!"
    my 123
    my doing foo
    my x 10
    my y 20
    my
      "Hello, world!"
      123
      doing foo
      x 10
      y 20
