use library number

comment
  recursive fibonacci

test
  16
  do recursing bind
    number
    is less than 2
    switch
      yes be number
      no be
        number.decrement.decrement.recurse
        plus number.decrement.recurse
  is equal 987
