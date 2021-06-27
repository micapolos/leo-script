test
  shape circle radius 10
  switch
    circle applying radius.number
    rectangle applying height.number
  is equal to 10

test
  shape rectangle height 10
  switch
    circle applying radius.number
    rectangle applying height.number
  is equal to 10

test
  try
    shape triangle height 10
    switch
      circle applying radius.number
      rectangle applying height.number
  is equal to quote try error switch
