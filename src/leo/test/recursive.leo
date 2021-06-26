comment
  recursive
    allows defining mutually recursive functions

test 
  recursive let
    any.number.countdown
    do
      countdown.number
      check equal to 0
      switch
        yes number
        no
          number
          minus 1
          countdown
  
  10.countdown
  is equal to 0
