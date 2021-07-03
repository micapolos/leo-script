comment
  repeat
    repeat the given script
  end
    ends repeating with the current value

test
  10000
  repeat
    check equal to 0
    select
      yes doing number.end
      no doing
        number
        minus 1
  is equal to 0
