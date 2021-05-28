use library text
  
let
  option any
  update present doing any
  do
    option
    switch
      present take update.present.doing
      absent option

test
  option present "Hello, "
  update present doing
    present.text
    append "world!"
  is equal "Hello, world!"

test
  option absent
  update present doing
    present.text
    append "world!"
  is equal option absent
