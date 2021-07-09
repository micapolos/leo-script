let
  any vector
  do vector
    x any number
    y any number

let
  any vector
  plus any vector
  do vector
    x
      vector.x.number
      plus plus.vector.x.number
    y
      vector.y.number
      plus plus.vector.y.number

let
  any vector
  length
  do length
    vector.x.number
    times vector.x.number
    plus
      vector.y.number
      times vector.y.number

vector
  x 20
  y 20
plus vector
  x 40
  y 60
length
