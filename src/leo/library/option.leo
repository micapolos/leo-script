use library text

let
  option any
  map present doing any
  do
    option
    switch
      present take map.present.doing
      absent option

test
  option present "Hello, "
  map present doing
    present.text
    append "world!"
  is equal "Hello, world!"

test
  option absent
  map present doing
    present.text
    append "world!"
  is equal option absent
