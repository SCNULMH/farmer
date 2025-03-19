import sys
import os
import torch
from torchvision import models, transforms
from PIL import Image

# 프로젝트 루트 디렉토리 기준 model.pth 파일 경로 설정
project_root = os.path.abspath(os.path.dirname(__file__))  # 현재 파일이 위치한 디렉토리
model_path = os.path.join(project_root, "model.pth")

# 모델 로드
if not os.path.exists(model_path):
    print(f"❌ 모델 파일을 찾을 수 없습니다: {model_path}")
    sys.exit(1)

model = models.resnet50(weights=None)
model.fc = torch.nn.Linear(model.fc.in_features, 10)  # 클래스 개수 (예: 10개)
model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
model.eval()

# 이미지 처리 및 예측
def predict(image_path):
    try:
        transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

        if not os.path.exists(image_path):
            return f"❌ 이미지 파일을 찾을 수 없습니다: {image_path}"

        image = Image.open(image_path).convert("RGB")
        image = transform(image).unsqueeze(0)
        output = model(image)
        _, predicted = torch.max(output, 1)

        disease_mapping = {
            0: "정상", 1: "고추점무늬병", 2: "고추마일드모틀바이러스병",
            3: "딸기잿빛곰팡이병", 4: "딸기흰가루병", 5: "참외노균병",
            6: "참외흰가루병", 7: "토마토잎곰팡이병",
            8: "토마토황화잎말이바이러스병", 9: "포도노균병"
        }

        return disease_mapping.get(predicted.item(), "알 수 없음")

    except Exception as e:
        return f"❌ 예측 오류: {str(e)}"

if __name__ == "__main__":
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        print(predict(image_path))
    else:
        print("❌ 이미지 파일 경로가 필요합니다.")
