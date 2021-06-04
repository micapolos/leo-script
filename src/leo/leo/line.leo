use library it
use leo field
use leo literal

let
  line any
  map doing any
  do bind
    line
    switch
      literal it
      field map map.doing
    line

test
  line literal 10
  map doing text
  is equal line literal 10

test
  line field
    "x"
    to 10
  map doing text
  is equal line field
    "x"
    to "10"
