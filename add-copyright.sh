#!/bin/bash

# Read copyright header from copyright.txt file
if [ ! -f "copyright.txt" ]; then
	echo "Error: copyright.txt file not found!"
	exit 1
fi

COPYRIGHT_HEADER=$(cat copyright.txt)
COPYRIGHT_HEADER="$COPYRIGHT_HEADER

"

echo "Adding LGPL v2.1 copyright headers to Java files..."

find . -name "*.java" -type f | while read -r file; do
	# Check if file already has copyright header
	if ! grep -q "Copyright (C)" "$file"; then
		echo "Adding header to: $file"

		# Create temporary file with copyright header + original content
		{
			echo "$COPYRIGHT_HEADER"
			cat "$file"
		} > "$file.tmp"

		# Replace original file
		mv "$file.tmp" "$file"
	else
		echo "Skipping $file - already has copyright header"
	fi
done

echo "Copyright header addition completed!"