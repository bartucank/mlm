import subprocess

libraries_to_install = ["requests", "flask", "flask_cors"]
for library in libraries_to_install:
    try:
        subprocess.run(["pip3", "install", library], check=False)
        print(f"{library} installed")
    except subprocess.CalledProcessError as e:
        print(f"Error !!! library: {library}  error: {e}")
from flask import Flask, request, jsonify
from datetime import datetime
import logging
from flask_cors import CORS


app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.INFO)


@app.route('/receive-data', methods=['POST', 'GET'])
def receive_data():
    if request.is_json:
        try:
            data = request.get_json()
            processed_data = process_data(data["queue"])

            return jsonify(processed_data), 200
        except Exception as e:
            logging.error("Error processing data: %s", str(e))
            return jsonify({"error": "Error processing data"}), 500
    else:
        return jsonify({"error": "Request must be JSON"}), 400

def avg_arrival_time(people_data):
    dates = [datetime.strptime(details['queue_date'], '%d/%m/%Y') for details in people_data.values()]
    borrow_dates = [datetime.strptime(details['borrow_date'], '%d/%m/%Y') for details in people_data.values()]

    date_differences = []
    for i in range(1, len(dates)):
        #Compare with previous on
        if dates[i] == dates[i - 1]:
            diff = (borrow_dates[i] - borrow_dates[i - 1]).days
        else:
            diff = (dates[i] - dates[i - 1]).days
        date_differences.append(diff)

    total_difference_sum = sum(date_differences)
    num_intervals = len(date_differences)

    avgArrivalTime = total_difference_sum / num_intervals
    return avgArrivalTime



def avg_service_time(people_data):
    total_service_time = 0
    for details in people_data.values():
        borrow_date = datetime.strptime(details['borrow_date'], '%d/%m/%Y')
        return_date = datetime.strptime(details['return_date'], '%d/%m/%Y')

        service_time = (return_date - borrow_date).days
        total_service_time += service_time

    avgServiceTime = total_service_time / len(people_data) if people_data else 0
    return avgServiceTime


def utilization_time(people_data):
    if(avg_arrival_time(people_data) != "There is only one person in the queue"):
        serverUtilization = avg_arrival_time(people_data) / avg_service_time(people_data)
        print("Serviceuti",serverUtilization)
        return serverUtilization
    else:
        return 0

def traffic_intensity(people_data):
    trafficIntensity = pow(utilization_time(people_data), 2)/(1-utilization_time(people_data))
    """
    if(trafficIntensity < 0):
        return "There is a high traffic which affects the system badly!!!!"
    elif(trafficIntensity > 1):
        return "Traffic intensity of the system very hight!!!! TrafficIntensity:", trafficIntensity
    elif(trafficIntensity > 0.8):
        return "Traffic intensity of the system very close to the unstability!!!! TrafficIntensity:", trafficIntensity
    elif(trafficIntensity == 0.0):
        return "There is a one person in the queue"
    """
    return trafficIntensity

def process_data(data):
    try:
        people_data = {}

        for i, person in enumerate(data, start=1):#Create a dictionary with keys starting from first person.
            queue_date = person.get('queueDate')
            borrow_date = person['borrowDate']
            return_date = person['returnDate']
            people_data[f'person_{i}'] = {
                'queue_date': queue_date,
                'borrow_date': borrow_date,
                'return_date': return_date
            }#Whenever we take the data no matter what it iterates the every persons data.
    except Exception as e:
        logging.error("Error in process_data: %s", str(e))
        raise

    print("Data received:", people_data)
    return traffic_intensity(people_data)


if __name__ == '__main__':
    app.run(debug=True, host='127.0.0.1', port=9010)