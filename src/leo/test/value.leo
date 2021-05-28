test
  point
    x 10
    y 20
  value.text
  is equal "point\n  x 10\n  y 20\n"

test
  "point\n  x 10\n  y 20\n"
  value.point
  is equal point
    x 10
    y 20
