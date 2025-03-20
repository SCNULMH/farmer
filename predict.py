import sys
import os
import torch
import torch.nn.functional as F
from torchvision import models, transforms
from PIL import Image

# 출력 인코딩 재설정 (UTF-8)
sys.stdout.reconfigure(encoding='utf-8')

# 현재 스크립트 위치를 기준으로 모델 파일 경로 지정
project_root = os.path.abspath(os.path.dirname(__file__))
model_path = os.path.join(project_root, "model.pth")

if not os.path.exists(model_path):
    print("\n❌ 모델 파일을 찾을 수 없습니다:", model_path, "\n")
    sys.exit(1)

try:
    # ResNet50 모델 생성: 10개의 클래스로 구성된 마지막 FC층
    model = models.resnet50(weights=None)
    model.fc = torch.nn.Linear(model.fc.in_features, 10)
    model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
    model.eval()
except Exception as e:
    print("\n❌ 모델 로드 실패:", e, "\n")
    sys.exit(1)

# 10개 클래스에 대한 질병 이름 매핑
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
            return "❌ 이미지 파일을 찾을 수 없습니다."

        transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

        image = Image.open(image_path).convert("RGB")
        image = transform(image).unsqueeze(0)

        # ✅ with torch.no_grad() 사용 (연산 추적 방지)
        with torch.no_grad():
            output = model(image)
            probabilities = F.softmax(output, dim=1)

        probs = probabilities.squeeze().cpu().numpy()  # ⬅️ 여기서 오류가 안 나게 됨!

        crop_valid_indices = {
            "고추": [0, 1, 2],
            "딸기": [0, 3, 4],
            "참외": [0, 5, 6],
            "토마토": [0, 7, 8],
            "포도": [0, 9]
        }

        if crop_name not in crop_valid_indices:
            return ("❌ 올바른 작물명이 아닙니다. "
                    "(고추, 딸기, 참외, 토마토, 포도 중 하나를 입력하세요.)")

        valid_indices = crop_valid_indices[crop_name]
        valid_probs = {i: probs[i] for i in valid_indices}
        predicted_index = max(valid_probs, key=valid_probs.get)
        confidence_percent = valid_probs[predicted_index] * 100

        if valid_probs[predicted_index] < 0.6:
            return (f"\n🦠 병해충 진단 결과:\n"
                    f"✅ 모델 로드 성공\n"
                    f"✅ 이미지 파일 전달됨\n"
                    f"✅ 예측 결과: 작물이 아닙니다. (신뢰도: {confidence_percent:.1f}%)")

        result = disease_mapping.get(predicted_index, "알 수 없음")
        return (f"\n🦠 병해충 진단 결과:\n"
                f"✅ 모델 로드 성공\n"
                f"✅ 이미지 파일 전달됨\n"
                f"✅ 예측 결과: {result} (신뢰도: {confidence_percent:.1f}%)")

    except Exception as e:
        return f"\n❌ 예측 오류: {str(e)}"


if __name__ == "__main__":
    # 명령행 인자로 작물명과 이미지 경로를 받아 예측 실행
    if len(sys.argv) > 2:
        crop_name = sys.argv[1]
        image_path = sys.argv[2]
        print(predict(crop_name, image_path))
    else:
        print("❌ 사용법: python predict.py [작물명] [이미지파일경로]")
