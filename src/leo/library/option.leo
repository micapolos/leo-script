use library text

let
  option any
  map doing any
  do option
    option
    switch
      present doing present
        map.doing
        give present.content
      absent doing absent

test
  option present "Hello, "
  map doing
    text
    append "world!"
  is equal option present "Hello, world!"

test
  option absent
  map doing
    text
    append "world!"
  is equal option absent
