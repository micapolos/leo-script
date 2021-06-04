use library number

test
  10000
  repeat
    check equal 0
    switch
      yes number.end
      no number.decrement
  is equal 0
