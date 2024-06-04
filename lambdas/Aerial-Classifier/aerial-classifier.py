import os
import tensorflow as tf
import requests
import json
from PIL import Image
from io import BytesIO
import numpy as np
import base64

current_dir = os.path.dirname(os.path.abspath(__file__))
local_model_path = os.path.join(current_dir, "static", "aerial_classifier.keras")
model = tf.keras.models.load_model(local_model_path)

aerial_image_labels = ["BareLand", "Beach", "DenseResidential", "Desert",
                       "Forest", "Mountain", "Parking", "SparseResidential"]

imageSize = 600
zoom = 15
maptype = "satellite"
api_key = os.getenv("GOOGLE_MAPS_API_KEY")

def handler(event, context):
    latitude = event.get("latitude")
    longitude = event.get("longitude")

    url = f"https://maps.googleapis.com/maps/api/staticmap?center={latitude},{longitude}&zoom={zoom}&size={imageSize}x{imageSize}&maptype={maptype}&key={api_key}"
    google_response = requests.get(url)

    if google_response.status_code == 200:
        image = Image.open(BytesIO(google_response.content))
        image = image.convert("RGB")

        # For getting the actual image
        buffered = BytesIO()
        image.save(buffered, format="JPEG")
        img_str = base64.b64encode(buffered.getvalue()).decode("utf-8")

        # Scale pixel values
        image_array = np.expand_dims(np.array(image) / 255.0, axis=0)
        prediction = model.predict(image_array)

        # Was including too much precision
        rounded_probabilities = [round(float(probability), 3) for probability in prediction[0]]

        predictions = {
            class_label: float(probability)
            for class_label, probability in zip(aerial_image_labels, rounded_probabilities)
        }

        return {
            "statusCode": 200,
            "body": json.dumps({
                "predictions": predictions,
                "image": img_str
            })
        }
    else:
        return {
            "statusCode": 504,
            "body": "Error processing image"
        }

# Local testing
# if __name__ == "__main__":
#
#     test_event = {
#         "latitude": 50,
#         "longitude": 75
#     }
#
#     response = handler(test_event, {})
#     print(response)