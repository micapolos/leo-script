let
  any.number.increment
  do
    number
    plus 1

test
  2.increment
  is equal 3

let
  any.number.decrement
  do
    number
    minus 1

test
  2.decrement
  is equal 1

let
  any.number.squared
  do 
    number
    bind
      number
      times number

test
  10.squared
  is equal 100
