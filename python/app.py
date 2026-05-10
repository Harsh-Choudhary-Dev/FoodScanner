from flask import Flask, request, jsonify
from flask_cors import CORS
import image_similarity as image_simi
import json

with open("temp_products.json") as f:
        data = json.load(f)

app = Flask(__name__)

# Enable CORS
CORS(app)

@app.route("/identify", methods=["POST"])
def identify_product():
    
    print(request.content_type)
    print(request.get_json())

    image_path = request.get_json().get("images_path").replace("file://","").replace("%20"," ")

    if not image_path:
        return jsonify({
            "error": "No image path found"
        }), 400
    
    results, distances = image_simi.search_similar(image_path, data)
    # Example response
    return jsonify({
        "product_name": results[0],
        "cnfidence":distances.tolist()
    })


if __name__ == "__main__":
    app.run(
        host="127.0.0.1",
        port=8765,
        debug=True
    )
