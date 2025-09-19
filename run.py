import os
import time
import requests
from pathlib import Path
import json

# -------------------- CONFIG --------------------
WATCH_DIR = r"/Users/yashcomputers/Desktop/upload"  # Folder to monitor
API_URL = "https://dsfiles.onrender.com/api/files/upload"
CHECK_INTERVAL = 300  # 5 minutes
TRACK_FILE = "uploaded_files.json"
VERIFY_SSL = True
# ------------------------------------------------

def load_uploaded_files():
    if os.path.exists(TRACK_FILE):
        with open(TRACK_FILE, "r") as f:
            return json.load(f)
    return {}

def save_uploaded_files(uploaded):
    with open(TRACK_FILE, "w") as f:
        json.dump(uploaded, f, indent=2)

def upload_file(file_path):
    print(f"Uploading: {file_path}")
    with open(file_path, "rb") as f:
        files = {"file": (os.path.basename(file_path), f)}
        try:
            response = requests.post(API_URL, files=files, verify=VERIFY_SSL)
            print(f"Response: {response.status_code} {response.text}")
            return response.status_code == 200
        except Exception as e:
            print(f"Failed to upload {file_path}: {e}")
            return False

def main():
    uploaded_files = load_uploaded_files()
    updated = False

    for file in Path(WATCH_DIR).glob("*"):
        if file.is_file():
            file_key = str(file.resolve())
            mod_time = file.stat().st_mtime

            if file_key not in uploaded_files or uploaded_files[file_key] < mod_time:
                success = upload_file(file)
                if success:
                    uploaded_files[file_key] = mod_time
                    updated = True

    if updated:
        save_uploaded_files(uploaded_files)
    else:
        print("No new or modified files to upload.")

if __name__ == "__main__":
    main()
