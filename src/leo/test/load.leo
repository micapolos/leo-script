comment
  load
    evaluates
      the script
      at the given location
    and
      caches the result
      for the subsequent loads

test
  load test loaded
  is equal to "Hello, world!"

test
  10
  load test loaded
  20
  load test loaded
  30
  is equal to
    10
    "Hello, world!"
    20
    "Hello, world!"
    30
