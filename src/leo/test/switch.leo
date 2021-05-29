test
  shape circle radius 10
  switch
    circle doing circle.radius
    rectangle doing rectangle.height
  is equal radius 10

test
  shape rectangle height 10
  switch
    circle doing circle.radius
    rectangle doing rectangle.height
  is equal height 10

test
  try
    shape triangle height 10
    switch
      circle doing circle.radius
      rectangle doing rectangle.height
  is equal quote try error switch
