types
  let
    location
    have
      latitude number
      longitude number
      altitude number
  let
    station
    have
      name text
      the location

let
  location.text
  do
    "location"
    plus "("
    plus
      "latitude: "
      plus latitude.number.text
    plus ", "
    plus
      "longitude: "
      plus longitude.number.text
    plus ", "
    plus
      "altitude: "
      plus altitude.number.text
    plus ")"

let
  station.text
  do
    "station"
    plus "("
    plus
      "name: '"
      plus name.text
      plus "'"
    plus ", "
    plus location.text
    plus ")"

station
  name "ALPHA 0"
  location
    latitude 10
    longitude 20
    altitude 30
text
