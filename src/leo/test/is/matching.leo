test
  "Hello, world!"
  is matching anything

test
  "Hello, world!"
  is matching text

test
  "Hello, world!"
  is matching text anything

test
  "Hello, world!"
  is not matching number

test
  123
  is matching number

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
    x number
    y number

test
  point
    x 10
    y 20
  is not matching point
    x text anything
    y text anything
