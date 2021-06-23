test
  point
    x 10
    y 20
  value.text
  is equal to "point\n  x 10\n  y 20\n"

test
  "point\n  x 10\n  y 20\n"
  value.point
  is equal to point
    x 10
    y 20
