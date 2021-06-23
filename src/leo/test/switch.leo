test
  shape circle radius 10
  switch
    circle radius.number
    rectangle height.number
  is equal to 10

test
  shape rectangle height 10
  switch
    circle radius.number
    rectangle height.number
  is equal to 10

test
  try
    shape triangle height 10
    switch
      circle radius.number
      rectangle height.number
  is equal to quote try error switch
