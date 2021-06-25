comment
  repeat
    repeat the given script
  end
    ends repeating with the current value

use library number

test
  10000
  repeat
    check equal to 0
    switch
      yes number.end
      no number.decrement
  is equal to 0
