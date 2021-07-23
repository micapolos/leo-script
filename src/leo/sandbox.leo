compile python
  types
    let
      vector
      have
        x number
        y number

  let
    number.square
    do
      number
      times number

  let
    vector.length
    do
      x.number.square
      plus y.number.square
      root

  vector
    x 10
    y 20
  length
