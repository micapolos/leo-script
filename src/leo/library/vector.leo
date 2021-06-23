use library number

let
  any.vector
  plus any.vector
  do vector
    x
      vector.x.number
      plus plus.vector.x.number
    y
      vector.y.number
      plus plus.vector.y.number

test
  vector
    x 10
    y 20
  plus vector
    x 40
    y 60
  is equal to vector
    x 50
    y 80
