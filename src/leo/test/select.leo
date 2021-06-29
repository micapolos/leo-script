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

test
  shape circle radius 10
  select
    circle doing
      pi.number
      times circle.radius.number
      times circle.radius.number
    rectangle doing
      rectangle.base.number
      times rectangle.height.number
    triangle doing
      triangle.base.number
      times triangle.height.number
      divided by 2
  is equal to
    pi.number
    times 100

test
  shape rectangle
    base 10
    height 15
  select
    circle doing
      pi.number
      times circle.radius.number
      times circle.radius.number
    rectangle doing
      rectangle.base.number
      times rectangle.height.number
    triangle doing
      triangle.base.number
      times triangle.height.number
      divided by 2
  is equal to 150

test
  shape triangle
    base 10
    height 15
  select
    circle doing
      pi.number
      times circle.radius.number
      times circle.radius.number
    rectangle doing
      rectangle.base.number
      times rectangle.height.number
    triangle doing
      triangle.base.number
      times triangle.height.number
      divided by 2
  is equal to 75
