test
  "Hello, world!"
  is matching anything

test
  "Hello, world!"
  is matching any text

test
  "Hello, world!"
  is matching text anything

test
  "Hello, world!"
  is not matching any number

test
  123
  is matching any number

test
  123
  is matching number anything

test
  point
    x 10
    y 20
  is matching anything

test
  point
    x 10
    y 20
  is matching point anything

test
  point
    x 10
    y 20
  is not matching circle anything

test
  point
    x 10
    y 20
  is matching point
    x number anything
    y number anything

test
  point
    x 10
    y 20
  is matching point
    x any number
    y any number

test
  point
    x 10
    y 20
  is not matching point
    x text anything
    y text anything
