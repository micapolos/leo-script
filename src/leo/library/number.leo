let
  any.number.increment
  do
    increment.number
    plus 1

test
  2.increment
  is equal to 3

let
  any.number.decrement
  do
    decrement.number
    minus 1

test
  2.decrement
  is equal to 1

let
  any.number.squared
  do
    squared.number
    times squared.number

test
  10.squared
  is equal to 100
