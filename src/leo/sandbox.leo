let
  any.result.make
  do result.content.success.result

test
  123.result.make
  is equal to 123.success.result

let
  any.result
  mtake any.doing
  do bind
    result
    switch
      success
        content
        take mtake.doing
      error result

test
  123.success.result
  mtake doing text.success.result
  is equal to "123".success.result

test
  123.success.result
  mtake doing napis.error.result
  is equal to 123.napis.error.result

test
  dupa.error.result
  mtake doing text.success.result
  is equal to dupa.error.result

test
  dupa.error.result
  mtake doing napis.error.result
  is equal to dupa.error.result
