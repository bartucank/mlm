import requests

url = 'http://localhost:9010/receive-data'

#queue_date,borrow_date,return_date
#data_to_send = ("9/07/2024,10/07/2024,20/07/2024;14/07/2024,20/07/2024,30/07/2024;"
             #   "15/07/2024,31/07/2024,10/08/2024;19/07/2024,10/08/2024,20/08/2024")
#data_to_send = ("15/07/2024,31/07/2024,10/08/2024;19/07/2024,10/08/2024,20/08/2024;"
           #    "23/07/2024,20/08/2024,30/08/2024;21/08/2024,30/08/2024,09/09/2024")
data_to_send = {
   "queue":[
      {
         "queueDate":"9/07/2024",
         "borrowDate":"9/07/2024",
         "returnDate":"20/07/2024"
      },
      {
         "queueDate":"9/07/2024",
         "borrowDate":"9/07/2024",
         "returnDate":"20/07/2024"
      },
      {
         "queueDate":"9/07/2024",
         "borrowDate":"20/07/2024",
         "returnDate":"30/08/2024"
      },
      {
         "queueDate":"19/07/2024",
         "borrowDate":"10/08/2024",
         "returnDate":"20/08/2024"
      }
   ]
}

response = requests.post(url, json=data_to_send)


print(response.json())

