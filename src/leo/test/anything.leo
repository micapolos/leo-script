comment
  anything
    matches any value
    when used in a pattern

test
  foo bar
  is matching anything

test
  color red
  is matching color anything

test
  10
  is matching number anything

test
  "Hello, world!"
  is matching text anything

test
  doing plus 1
  is matching doing anything

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
  is matching point
    x anything
    y anything

test
  point
    x 10
    y 20
  is matching point
    x number anything
    y number anything
