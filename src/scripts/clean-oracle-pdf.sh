#!/bin/bash

IN="$1"
OUT="$2"
LOG="/opt/eisenvault_installations/alfresco/logs/oracle_pdf_cleaner.log"

# Create log directory if missing
mkdir -p "$(dirname "$LOG")"

{
    echo "----"
    echo "$(date): Starting transformation"
    echo "Input: $IN"
    echo "Output: $OUT"

    info=$(pdfinfo "$IN")
    producer=$(echo "$info" | grep -i '^Producer:' | awk -F': ' '{print $2}')
    creator=$(echo "$info" | grep -i '^Creator:' | awk -F': ' '{print $2}')

    echo "Producer: $producer"
    echo "Creator : $creator"

    if [[ "$producer" == *Oracle* || "$creator" == *Oracle* ]]; then
        echo "Detected Oracle PDF. Cleaning..."
        /usr/bin/gs -sDEVICE=pdfwrite -dCompatibilityLevel=1.3 -dPDFSETTINGS=/printer \
           -dNOPAUSE -dQUIET -dBATCH -sOutputFile="$OUT" "$IN"

        if [[ $? -eq 0 ]]; then
            echo "Cleaning successful."
        else
            echo "Ghostscript failed!"
            exit 1
        fi
    else
        echo "Not an Oracle PDF. Passing original through."
        cp "$IN" "$OUT"
    fi
    echo "Transformation complete"
} >> "$LOG" 2>&1
