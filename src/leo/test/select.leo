test
  is yes
  select
    yes doing "OK"
    no doing "not OK"
  is equal to "OK"

test
  is no
  select
    yes doing "OK"
    no doing "not OK"
  is equal to "not OK"

test
  10
  is equal to 20
  select
    yes doing "10 == 20"
    no doing "10 != 20"
  is equal to "10 != 20"

test
  id 123
  select
    number applying plus 1
    text applying append "!"
  is equal to 124

test
  id "foo"
  select
    number applying plus 1
    text applying append "!"
  is equal to "foo!"
