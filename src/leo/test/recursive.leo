use library number

test 
  recursive let
    any.number.down.count
    do
      count.down.number
      check equal 0
      switch
        yes number
        no number.decrement.down.count
  count down 10
  is equal 0
