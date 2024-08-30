import os
import streamlit as st
import google.generativeai as genai
from google.ai.generativelanguage_v1beta.types import content
from PIL import Image
import tempfile
from dotenv import load_dotenv

load_dotenv()

# Configure the API key
genai.configure(api_key=os.environ.get("GEMINI_AI_API_KEY"))

def upload_to_gemini(file_path, mime_type=None):
    """Uploads the file to Gemini."""
    try:
        # Upload file content using the file path
        file = genai.upload_file(file_path, mime_type=mime_type)
        
        st.write(f"Uploaded file as: {file.uri}")
        return file
    except Exception as e:
        st.error(f"Failed to upload file: {str(e)}")
        return None

def process_image(file_object, mime_type):
    """Process the uploaded image with the Generative AI model."""
    # Save the file to a temporary location
    with tempfile.NamedTemporaryFile(delete=False) as tmp_file:
        tmp_file.write(file_object.read())
        tmp_file_path = tmp_file.name

    gemini_file = upload_to_gemini(tmp_file_path, mime_type)

    if gemini_file:
        model = genai.GenerativeModel(
            model_name="gemini-1.5-pro",
            generation_config={
                "temperature": 1.0,
                "top_p": 0.95,
                "top_k": 64,
                "max_output_tokens": 8192,
                "response_schema": content.Schema(
                    type=content.Type.OBJECT,
                    required=["response"],
                    properties={
                        "response": content.Schema(
                            type=content.Type.OBJECT,
                            required=["food_name"],
                            properties={
                                "food_name": content.Schema(
                                    type=content.Type.STRING,
                                ),
                            },
                        ),
                    },
                ),
                "response_mime_type": "application/json",
            },
            system_instruction=(
                "I will give you the images of foods. All images are about foods. "
                "Determine the food name and give me back the name of the food accurately."
            ),
        )

        # Start a chat session and send the image
        chat_session = model.start_chat()
        response = chat_session.send_message(gemini_file)
        return response.text
    
    # Remove the temporary file
    os.remove(tmp_file_path)

    return None

# Streamlit App
st.title("Food Image Recognition")

# Uploading option
upload_option = st.selectbox("Choose upload option", ["Upload from File", "Take a Picture with Camera"])

if upload_option == "Upload from File":
    uploaded_file = st.file_uploader("Choose an image file", type=["jpg", "jpeg", "png"])
    if uploaded_file is not None:
        image = Image.open(uploaded_file)
        st.image(image, caption="Uploaded Image", use_column_width=True)
        response = process_image(uploaded_file, mime_type="image/jpeg")
        if response:
            st.write("Model Response:", response)

elif upload_option == "Take a Picture with Camera":
    picture = st.camera_input("Take a picture")
    if picture is not None:
        image = Image.open(picture)
        st.image(image, caption="Captured Image", use_column_width=True)
        response = process_image(picture, mime_type="image/jpeg")
        if response:
            st.write("Model Response:", response)