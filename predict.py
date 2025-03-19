import sys
import os
import torch
import torch.nn.functional as F
from torchvision import models, transforms
from PIL import Image

sys.stdout.reconfigure(encoding='utf-8')

project_root = os.path.abspath(os.path.dirname(__file__))
model_path = os.path.join(project_root, "model.pth")

if not os.path.exists(model_path):
    print("\n❌ 모델 파일을 찾을 수 없습니다:", model_path, "\n")
    sys.exit(1)

try:
    model = models.resnet50(weights=None)
    model.fc = torch.nn.Linear(model.fc.in_features, 10)
    model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
    model.eval()
except Exception as e:
    print("\n❌ 모델 로드 실패:", e, "\n")
    sys.exit(1)

def predict(image_path):
    try:
        if not os.path.exists(image_path):
            return "❌ 이미지 파일을 찾을 수 없습니다."

        transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

        image = Image.open(image_path).convert("RGB")
        image = transform(image).unsqueeze(0)
        output = model(image)
        probabilities = F.softmax(output, dim=1)
        confidence, predicted = torch.max(probabilities, 1)
        confidence_percent = confidence.item() * 100  # 신뢰도 백분율 계산

        # 신뢰도가 60% 이하이면 "작물이 아님"으로 반환
        if confidence.item() < 0.6:
            return (f"\n🦠 병해충 진단 결과:\n"
                    f"✅ 모델 로드 성공\n"
                    f"✅ 이미지 파일이 전달됨\n"
                    f"✅ 예측 결과: 작물이 아닙니다. (신뢰도: {confidence_percent:.1f}%)")

        disease_mapping = {
            0: "정상", 1: "고추점무늬병", 2: "고추마일드모틀바이러스병",
            3: "딸기잿빛곰팡이병", 4: "딸기흰가루병", 5: "참외노균병",
            6: "참외흰가루병", 7: "토마토잎곰팡이병",
            8: "토마토황화잎말이바이러스병", 9: "포도노균병"
        }

        result = disease_mapping.get(predicted.item(), "알 수 없음")
        return (f"\n🦠 병해충 진단 결과:\n"
                f"✅ 모델 로드 성공\n"
                f"✅ 이미지 파일이 전달됨\n"
                f"✅ 예측 결과: {result} (신뢰도: {confidence_percent:.1f}%)")

    except Exception as e:
        return f"\n❌ 예측 오류: {str(e)}"


if __name__ == "__main__":
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        print(predict(image_path))
    else:
        print("❌ 이미지 파일 경로가 필요합니다.")
