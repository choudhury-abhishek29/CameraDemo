from flask import Flask
from flask import request
from werkzeug.utils import secure_filename
from subprocess import call
import pytesseract
from PIL import Image
import os
import uuid
import zbar
import subprocess

app = Flask(__name__)

UPLOAD_FOLDER = "/home/ubuntu/visonus/uploads"


@app.route("/text", methods=['POST'])
def text():
    if "image" not in request.files:
        return "Bad Request", 400
    f = request.files['image']
    image_path = os.path.join(UPLOAD_FOLDER, secure_filename(f.filename) + str(uuid.uuid4()))
    f.save(image_path)
    image = Image.open(image_path)
    text = pytesseract.image_to_string(image)
    return text
	
@app.route("/view", methods=['GET'])
def view():
    return "test message"
    
@app.route("/code", methods=['POST'])
def code():
    if "image" not in request.files:
        return "Bad Request", 400
    f = request.files['image']
    image_path = os.path.join(UPLOAD_FOLDER, secure_filename(f.filename) + str(uuid.uuid4()))
    f.save(image_path)
    scanner = zbar.ImageScanner()
    scanner.parse_config('enable')
    pil = Image.open(image_path).convert('L')
    width, height = pil.size
    raw = pil.tobytes()
    image_op = zbar.Image(width, height, 'Y800', raw)
    scanner.scan(image_op)
    for symbol in image_op:
        return symbol.data

if __name__ == "__main__":
    app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
    app.run(host="0.0.0.0", port=8080)
