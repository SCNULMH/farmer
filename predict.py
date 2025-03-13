import sys
import torch
from torchvision import models, transforms
from PIL import Image

# 모델 로드 (모델 경로 설정)
model_path = "D:/dataset/saved_models/model.pth"  # 모델 경로 지정
model = models.resnet50(weights=None)
model.fc = torch.nn.Linear(model.fc.in_features, 10)  # 예시: 10개의 클래스
model.load_state_dict(torch.load(model_path))  # 모델 로드
model.eval()

# 이미지 처리 및 예측
def predict(image_path):
    transform = transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
    ])

    image = Image.open(image_path).convert("RGB")
    image = transform(image).unsqueeze(0)
    output = model(image)
    _, predicted = torch.max(output, 1)
    
    # 예시: 반환할 질병 코드 (0~9)
    disease_mapping = {0: "정상", 1: "고추점무늬병", 2: "고추마일드모틀바이러스병", 3: "딸기잿빛곰팡이병", 4: "딸기흰가루병", 5: "참외노균병", 6: "참외흰가루병", 7: "토마토잎곰팡이병", 8: "토마토황화잎말이바이러스병", 9: "포도노균병"}
    
    return disease_mapping.get(predicted.item(), "알 수 없음")

if __name__ == "__main__":
    image_path = sys.argv[1]
    result = predict(image_path)
    print(result)  # 예측된 결과 출력