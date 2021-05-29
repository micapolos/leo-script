use library number

comment
  recursive fibonacci

test
  16
  do recursing
    number
    is less than 2
    switch
      yes doing number
      no doing
        number.decrement.decrement.recurse
        plus number.decrement.recurse
  is equal 987
