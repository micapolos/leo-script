types
  let
    nat
    be recursive nat choice
      zero
      previous recurse

nat zero
make nat previous
switch
  zero doing 0
  previous doing
    previous
    switch
      zero doing 1
      previous doing 9999