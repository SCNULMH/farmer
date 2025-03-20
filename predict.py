import sys
import os
import json
import torch
import torch.nn.functional as F
from torchvision import models, transforms
from PIL import Image

sys.stdout.reconfigure(encoding='utf-8')

project_root = os.path.abspath(os.path.dirname(__file__))
model_path = os.path.join(project_root, "model.pth")

if not os.path.exists(model_path):
    print(json.dumps({"error": "❌ 모델 파일을 찾을 수 없습니다."}, ensure_ascii=False))
    sys.exit(1)

try:
    model = models.resnet50(weights=None)
    model.fc = torch.nn.Linear(model.fc.in_features, 10)
    model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
    model.eval()
except Exception as e:
    print(json.dumps({"error": f"❌ 모델 로드 실패: {str(e)}"}, ensure_ascii=False))
    sys.exit(1)

disease_mapping = {
    0: "정상",
    1: "고추점무늬병",
    2: "고추마일드모틀바이러스병",
    3: "딸기잿빛곰팡이병",
    4: "딸기흰가루병",
    5: "참외노균병",
    6: "참외흰가루병",
    7: "토마토잎곰팡이병",
    8: "토마토황화잎말이바이러스병",
    9: "포도노균병"
}

def predict(crop_name, image_path):
    try:
        if not os.path.exists(image_path):
            return json.dumps({"error": "❌ 이미지 파일을 찾을 수 없습니다."}, ensure_ascii=False)

        transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

        image = Image.open(image_path).convert("RGB")
        image = transform(image).unsqueeze(0)

        with torch.no_grad():
            output = model(image)
            probabilities = F.softmax(output, dim=1)

        probs = probabilities.squeeze().cpu().numpy()

        crop_valid_indices = {
            "고추": [0, 1, 2],
            "딸기": [0, 3, 4],
            "참외": [0, 5, 6],
            "토마토": [0, 7, 8],
            "포도": [0, 9]
        }

        if crop_name not in crop_valid_indices:
            return json.dumps({"error": "❌ 올바른 작물명이 아닙니다. (고추, 딸기, 참외, 토마토, 포도 중 하나를 입력하세요.)"}, ensure_ascii=False)

        valid_indices = crop_valid_indices[crop_name]
        valid_probs = {i: probs[i] for i in valid_indices}
        predicted_index = max(valid_probs, key=valid_probs.get)
        confidence_percent = valid_probs[predicted_index] * 100

        if confidence_percent < 60:
            return json.dumps({
                "prediction": "작물이 아닙니다.",
                "confidence": f"{confidence_percent:.1f}%"
            }, ensure_ascii=False)

        result = disease_mapping.get(predicted_index, "알 수 없음")

        return json.dumps({
            "병해충 진단 결과": result,
            "정확도": f"{confidence_percent:.1f}%"
        }, ensure_ascii=False)
        
        
    except Exception as e:
        return json.dumps({"error": f"❌ 예측 오류: {str(e)}"}, ensure_ascii=False)


if __name__ == "__main__":
    if len(sys.argv) > 2:
        crop_name = sys.argv[1]
        image_path = sys.argv[2]
        print(predict(crop_name, image_path))
    else:
        print(json.dumps({"error": "❌ 사용법: python predict.py [작물명] [이미지파일경로]"}, ensure_ascii=False))
