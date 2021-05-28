test
  shape circle radius
  switch
    circle red
    rectangle blue
  is equal
    red circle radius

test
  shape rectangle side
  switch
    circle red
    rectangle blue
  is equal
    blue rectangle side

test
  try
    shape triangle height
    switch
      circle red
      rectangle blue
  is equal quote try error nonexaustive
