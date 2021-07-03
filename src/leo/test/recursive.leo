comment
  recursive
    allows defining mutually recursive functions

test 
  recursive let
    number.countdown
    do
      number
      check equal to 0
      select
        yes doing number
        no doing
          number
          minus 1
          countdown
  
  10.countdown
  is equal to 0
