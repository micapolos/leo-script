let
  number.any.previous
  do
    number
    minus 1

25.fibonacci
do repeating
  number
  is less than 2
  switch
    yes number
    no
      number.decrement.decrement.fibonacci
      plus number.decrement.fibonacci
