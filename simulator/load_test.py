import json
import random
import threading
import time

import requests

BASE_URL = "http://localhost:8080"

LOGIN_URL = f"{BASE_URL}/api/auth/token"
INGESTION_URL = f"{BASE_URL}/api/ingestion/records"

USERNAME = "admin"
PASSWORD = "admin"

VEHICLE_IDS = [f"CAR{i:03}" for i in range(1, 51)]

THREADS = 2
REQUEST_INTERVAL = 1

token = None


def login():
    global token

    response = requests.post(
        LOGIN_URL,
        json={
            "username": USERNAME,
            "password": PASSWORD
        }
    )

    response.raise_for_status()

    token = response.json()["token"]

    print("Authenticated successfully")


def generate_payload():
    vehicle = random.choice(VEHICLE_IDS)

    telemetry = {
        "vehicleId": vehicle,
        "speed": random.randint(0, 140),
        "rpm": random.randint(700, 4500),
        "fuel": random.randint(10, 100),
        "engineTemp": random.randint(70, 105),
        "timestamp": int(time.time() * 1000)
    }

    return {
        "source": "vehicle-simulator",
        "payload": json.dumps(telemetry)
    }


def worker(worker_id):
    global token

    while True:

        payload = generate_payload()

        try:

            response = requests.post(
                INGESTION_URL,
                json=payload,
                headers={
                    "Authorization": f"Bearer {token}"
                },
                timeout=10
            )

            if response.status_code == 401:
                print("Token expired. Logging in again...")
                login()
                continue

            print(
                f"[Worker-{worker_id}] "
                f"{response.status_code}"
            )

        except Exception as e:
            print(f"[Worker-{worker_id}] ERROR: {e}")

        time.sleep(REQUEST_INTERVAL)


if __name__ == "__main__":

    login()

    for i in range(THREADS):
        threading.Thread(
            target=worker,
            args=(i + 1,),
            daemon=True
        ).start()

    while True:
        time.sleep(1)