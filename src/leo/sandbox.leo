let
  number.decrement
  do
    number
    minus 1

10.fibonacci
repeat
  giving number
  doing
    number
    is less than 2
    switch
      yes doing number
      no doing
        number.decrement.decrement.fibonacci
        plus number.decrement.fibonacci
