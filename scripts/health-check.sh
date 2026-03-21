#!/bin/bash
set -euo pipefail

URL="${1:-http://localhost:8080/actuator/health}"
MAX_RETRIES="${2:-20}"
SLEEP_SECONDS="${3:-3}"

echo "Health check target: ${URL}"

for ((i=1; i<=MAX_RETRIES; i++)); do
  RESPONSE="$(curl -fsS "${URL}" || true)"

  if echo "${RESPONSE}" | grep -q '"status":"UP"'; then
    echo "Health check passed"
    echo "${RESPONSE}"
    exit 0
  fi

  echo "[${i}/${MAX_RETRIES}] waiting for application to become healthy..."
  sleep "${SLEEP_SECONDS}"
done

echo "Health check failed"
exit 1