use library number

test 
  recursive let
    any.number.down.count
    do bind
      count.down.number
      check equal to 0
      switch
        yes number
        no number.decrement.down.count
  10.down.count
  is equal to 0
