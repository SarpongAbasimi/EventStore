#!/bin/bash
echo "Rule No1 - Ensure that the server is running 🎲"
echo "Sending request to localhost:8888/api/v1/thoughts"

for i in {1..555}
do
  curl --request POST -sL -i \
       --header "Content-Type: application/json" \
       --url 'http://localhost:8888/api/v1/thoughts'\
       --data '{
                "name" : "Nana Kumakumaa",
                "message" : "I made a lot of progress with Scala today",
                "regrets" : "I lived today to the fullest so I have no regrets"
            }'
  echo "Request number $i "
done
