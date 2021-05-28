test
  "Hello, world!"
  is matching text any

test
  "Hello, world!"
  is not matching text

test
  123
  is matching number any

test
  123
  is not matching number

test
  point
    x 10
    y 20
  is matching point any

test
  point
    x 10
    y 20
  is not matching circle any

test
  point
    x 10
    y 20
  is matching point
    x number any
    y number any

test
  point
    x 10
    y 20
  is not matching point
    x text any
    y text any
