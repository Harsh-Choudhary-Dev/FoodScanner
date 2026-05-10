import torch
import clip
from PIL import Image
import json
import time


# ----------------------------
# 1. Load Model
# ----------------------------
def load_clip_model():
    device = "cuda" if torch.cuda.is_available() else "cpu"
    model, preprocess = clip.load("ViT-B/32", device=device)
    return model, preprocess, device


# ----------------------------
# 2. Encode Image
# ----------------------------
def encode_image(image_path, model, preprocess, device):
    image = preprocess(Image.open(image_path)).unsqueeze(0).to(device)
    
    with torch.no_grad():
        image_features = model.encode_image(image)
        image_features /= image_features.norm(dim=-1, keepdim=True)
    
    return image_features


# ----------------------------
# 3. Encode Text Prompts
# ----------------------------
def encode_text(prompts, model, device):
    text = clip.tokenize(prompts).to(device)
    
    with torch.no_grad():
        text_features = model.encode_text(text)
        text_features /= text_features.norm(dim=-1, keepdim=True)
    
    return text_features


# ----------------------------
# 4. Classify Image
# ----------------------------
def classify_image(image_path, model, preprocess, device, text_features,filtering = 1):
    if filtering == 1:
        labels = ["front", "back", "banner", "text"]
    else:
        labels = ["banner", "product"]

    image_features = encode_image(image_path, model, preprocess, device)

    similarity = (image_features @ text_features.T).softmax(dim=-1)
    probs = similarity.cpu().numpy()[0]

    # Get best prediction
    max_index = probs.argmax()
    predicted_label = labels[max_index]

    result = {
        "label": predicted_label,
        "scores": {
            labels[i]: float(probs[i]) for i in range(len(labels))
        }
    }

    return result

def save_json(data, file_path):
    with open(file_path, "w") as f:
        json.dump(data, f, indent=2)


def main():
    product_info_json = "backend/products_info.json"

    model, preprocess, device = load_clip_model()
    prompts = [
    "a clean single packaged food product centered with plain background and no promotional text",
    "the back side of a packaged food product showing ingredients and nutrition facts with dense text",
    "a colorful promotional banner with offers, discounts, multiple products and large text like sale or combo",
    "an image filled with text such as ingredients, license number, or nutritional values with very little product visible Fssai numbers"
]
    
    prompts_2 = [
        "A high-quality commercial food advertisement for Indian savory snacks, featuring colorful branded packaging and crunchy namkeen pieces floating in mid-air, vibrant solid backgrounds with radial gradients, professional studio lighting, and happy people sharing snacks in a modern lifestyle setting.",
        "A high-definition commercial product photograph of Indian snack packaging and potato chips, featuring vibrant orange and red color palettes, textured ridged crisps with visible seasoning, hand-held interaction shots, and comparison layouts against wooden or solid-colored backgrounds, professional marketing photography style."

    ]

    text_features = encode_text(prompts, model, device)

    with open(product_info_json) as file:
        data = json.load(file)

    # 🔥 Count total images
    total_images = sum(
        1 for product in data for image in product["images"] if image["image_filter_result"]["label"] == "front"
    )

    print(f"Total images to process: {total_images}\n")

    processed = 0
    start_time = time.time()

    for products in data:
        for image in products["images"]:
            # if image["image_filter_result"]["label"] == "front":

                image_path = image["product_image_path"]

                result = classify_image(
                    image_path,
                    model,
                    preprocess,
                    device,
                    text_features,
                    filtering=2

                )

                image["image_filter_result"] = result

                processed += 1

                # ⏱️ Time tracking
                elapsed = time.time() - start_time
                avg_time = elapsed / processed
                remaining = total_images - processed
                eta = avg_time * remaining

                # 📊 Logs
                print(
                    f"[{processed}/{total_images}] "
                    f"{result['label']} | "
                    f"Time elapsed: {round(elapsed, 2)}s | "
                    f"ETA: {round(eta, 2)}s"
                )

        # ✅ Save 
        save_json(data, product_info_json)

    total_time = time.time() - start_time
    print(f"\n✅ Done! Total time: {round(total_time, 2)} seconds")


def image_filter_2():
    pass


# ----------------------------
# Run
# ----------------------------
if __name__ == "__main__":
    main()

