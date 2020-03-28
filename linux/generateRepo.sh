#!/bin/bash
###################################################################
#Script Name	:generateRepo.sh                                                                                    
#Description	:Generate meta data for ArmA 3 Launcher
#Date           :24.03.2020                                                                
#Author       	:Niklas Schütrumpf                                                
#Email         	:niklas@mc8051.de                                           
###################################################################

if ! [ -x "$(command -v zsyncmake)" ]; then
  echo 'Error: zsync is not installed.' >&2
  exit 1
fi

if ! [ -x "$(command -v jq)" ]; then
  echo 'Error: jq is not installed.' >&2
  exit 1
fi

if ! [ -x "$(command -v strings)" ]; then
  echo 'Error: binutils is not installed.' >&2
  exit 1
fi

declare -A SHASUMS

echo "===== ===== ===== GENERATE .ZSYNC ===== ===== ====="
FILELIST=$(find . -type f ! -path "*/.sync*" ! -path "*.zsync")
while IFS= read -r line; do
    mustgenerate=false
    zsyncfile="${line}.zsync"

    filebyte=$(wc -c < "${line}")
    filedate=$(stat -c %Y "${line}")

    zsyncfiledate=$(strings "${zsyncfile}" 2>/dev/null | grep -m 1 MTime | cut -d" " -f2-)

    if [ ! -f "$zsyncfile" ]; then
        echo "$zsyncfile does not exist"
        mustgenerate=true
    elif [[ ! $(strings "${zsyncfile}" | grep -m 1 Length | cut -d" " -f2) == $filebyte ]]; then # Check file length
        echo "$zsyncfile does not have corret length"
        mustgenerate=true
    elif [[ ! $filedate == $(date -d "${zsyncfiledate}" +"%s") ]]; then # Check date
        echo "$zsyncfile does not have corret date"
        mustgenerate=true
    fi

    if [ "$mustgenerate" = true ]; then
        echo "Generate $zsyncfile"
        rm "${zsyncfile}" 2> /dev/null
        dirfile=$(dirname "${line}")
        filename=$(basename "${line}")
        filenamezsync=$(basename "${zsyncfile}")
        $(cd "${dirfile}" && zsyncmake -o "${filenamezsync}" "${filename}")
        if [ $? -eq 0 ]; then
            echo "Success: Generated ${zsyncfile}"
        else
            echo "Failure: Couldn't generate ${zsyncfile}" >&2
        fi
    else
        echo "Nothing changed for $line"
    fi

    SHA=$(strings "${zsyncfile}" | grep -m 1 SHA-1 | cut -d" " -f2)
    SHASUMS[$(echo "${line}" | sed 's|^./||')]="${SHA}"

done <<< "$FILELIST"
echo -e "===== ===== ===== ===== ===== =====\n"

echo "===== ===== ===== DELETE SINGLE ZFILE WITHOUT FILE ===== ===== ====="
ZSYNCLIST=$(find . -name "*.zsync")
while IFS= read -r zfile; do
    ORIG=$(echo "${zfile}" | rev | cut -c7- | rev)
    if [ ! -f "$ORIG" ]; then
        echo "$ORIG does not exist"
        rm "${zfile}"
    fi
done <<< "$ZSYNCLIST"
echo -e "===== ===== ===== ===== ===== =====\n"

echo "===== ===== ===== GENERATE METADATA ===== ===== ====="
FILELIST=$(find . -maxdepth 1 ! -path "*/.sync*" ! -path "*.zsync" ! -path "." | sed 's|^./||')
declare -a JSONDATA
while IFS= read -r folder; do
    echo "${folder}"

    if [ -d "$folder" ]; then
        echo "is dir"
        x=""
        foldersize=0
        FILEFOLDER=$(find "${folder}" -type f ! -path "*.zsync" | sed 's|^./||')
        while IFS= read -r folderfile; do
            filebyte=$(wc -c < "${folderfile}")

            if [ $filebyte -eq 0  ]; then
              echo "Skipping \"${folderfile}\" because file is empty"
              continue
            fi

            foldersize=$(expr $foldersize + $filebyte)
            name=$(echo "${folderfile}" | cut -d"/" -f2-)
            x="\"${name}\":{\"size\": ${filebyte}, \"sha1\": \"${SHASUMS[$folderfile]}\"},${x}"
        done <<< "$FILEFOLDER"
        x=$(echo ${x} | rev | cut -c2- | rev)

        if [ $foldersize -eq 0  ]; then
            echo "Skipping complete folder \"${$folder}\" because all files are empty"
          continue
        fi
        JSONDATA+=( "\"${folder}\": {\"size\":${foldersize},\"content\":{${x}}}" )
    else
        echo "is file"
        filebyte=$(wc -c < "${folder}")
        if [ $filebyte -eq 0  ]; then
         continue
        fi
        JSONDATA+=( "\"${folder}\": {\"size\":${filebyte}, \"sha1\": \"${SHASUMS[$folder]}\"}" )
    fi
done <<< "$FILELIST"

s=""
for i in "${JSONDATA[@]}"
do
   s="${s},${i}"
done

s=$(echo ${s} | cut -c2-)
s="{${s}}"

echo $s | jq . > /dev/null

if [ $? -eq 0 ]; then
    echo $s > ./.sync/modset.json
    echo "Success: Generated metafile"
else
    echo "Failure: invalid json generated" >&2
fi

echo -e "===== ===== ===== ===== ===== =====\n"