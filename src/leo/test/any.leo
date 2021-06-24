test
  red.color
  is matching any.color

test
  10
  is matching any.number

test
  "Hello, world!"
  is matching any.text

test
  doing plus 1
  is matching any.doing

test
  point
    x 10
    y 20
  is matching any

test
  point
    x 10
    y 20
  is matching any.point

test
  point
    x 10
    y 20
  is matching point
    x any
    y any

test
  point
    x 10
    y 20
  is matching point
    x any.number
    y any.number
