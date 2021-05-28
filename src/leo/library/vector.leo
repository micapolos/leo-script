use library number

let
  vector any
  plus vector any
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
  is equal vector
    x 50
    y 80
