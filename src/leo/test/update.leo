test
  point
    x zero
    y one
  update x plus one
  is equal to point
    x
      zero
      plus one
    y one

test
  point
    x zero
    y one
    z two
  update
    x plus one
    z plus three
  is equal to point
    x
      zero
      plus one
    y one
    z
      two
      plus three

test
  try 
    point
      x zero
      y one
    update
      z plus three
  is equal to quote try error no field z
