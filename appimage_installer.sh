#!/bin/bash

file=$1
app_name=$2
target_directory="/opt"

if [ ! -f "$file" ]; then
	echo "File doesn't exist. Exiting"
	exit 1
fi

if [[ ! "${file,,}" =~ \.appimage$ ]]; then
	echo "Not an AppImage file. Exiting."
	exit 1
fi

chmod +x "${file}"

sudo mkdir -p "${target_directory}/${app_name}"

target_file="${target_directory}/${app_name}/${app_name}.AppImage"

sudo mv "${file}" "${target_file}"

sudo ln -sf "${target_file}" "/usr/local/bin/${app_name}"


desktop_entry="[Desktop Entry]
Name=${app_name}
Exec=${target_file}
Type=Application
Categories=Utility;
Terminal=false"

if [ -n "${icon_path}" ]; then 
	desktop_entry="${desktop_entry}
Icon=${icon_path}"
fi

echo "${desktop_entry}" | sudo tee  "/usr/share/applications/${app_name}.desktop" > /dev/null

sudo update-desktop-database
