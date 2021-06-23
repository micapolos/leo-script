comment
  recursive fibonacci

test
  16
  do recursing
    check less than 2
    switch
      yes number
      no bind
        no.number
        minus 2
        recurse
        plus
          no.number
          minus 1
          recurse
  is equal to 987
