test
  point
    x 10
    y 20
  set x 30
  is equal to point
    x 30
    y 20

test
  point
    x 10
    y 20
    z 30
  set
    x 40
    z 50
  is equal to point
    x 40
    y 20
    z 50

test
  try
    point
      x 10
      y 20
    set z 30
  is equal to quote try error no field z
