use library text

let
  option any
  map doing any
  do
    option
    switch
      present doing
        map.doing
        give present.content
        present
      absent doing absent
    option

test
  "Hello, ".present.option
  map doing
    text
    append "world!"
  is equal "Hello, world!".present.option

test
  absent.option
  map doing
    text
    append "world!"
  is equal absent.option
