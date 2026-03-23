#!/bin/bash
set -e

IMAGE_NAME="travel-journal-backend"
IMAGE_TAG="${1}"
CONTAINER_NAME="travel-journal-app"
APP_PORT="8080"
HOST_PORT="8082"
ENV_FILE="/volume1/docker/travel-journal/.env"

if [ -z "$IMAGE_TAG" ]; then
  echo "사용법: ./scripts/deploy-main.sh <image_tag>"
  exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
  echo "환경파일이 없습니다: $ENV_FILE"
  exit 1
fi

echo "[1/5] 기존 컨테이너 중지 및 삭제"
docker rm -f ${CONTAINER_NAME} 2>/dev/null || true

echo "[2/5] 새 이미지로 컨테이너 실행"
docker run -d \
  --name ${CONTAINER_NAME} \
  -p ${HOST_PORT}:${APP_PORT} \
  --env-file ${ENV_FILE} \
  ${IMAGE_NAME}:${IMAGE_TAG}

echo "[3/5] 애플리케이션 기동 대기"
sleep 15

echo "[4/5] Health Check 확인"
curl -f http://localhost:${HOST_PORT}/actuator/health

echo "[5/5] 배포 완료"