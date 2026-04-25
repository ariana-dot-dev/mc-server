#!/usr/bin/env bash
set -euo pipefail

if [ $# -eq 0 ]; then
    echo "Usage: ascii-say.sh <message>"
    exit 1
fi

MESSAGE="$*"
screen -S minecraft -p 0 -X stuff "ascii-say $MESSAGE\r"
