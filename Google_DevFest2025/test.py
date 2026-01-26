import re
import google.generativeai as genai
from PIL import Image
import json
from dotenv import load_dotenv
from model import load_creds

load_dotenv()

creds = load_creds()

genai.configure(credentials=creds)

GEMINI_PRO = genai.GenerativeModel('gemini-pro')
GEMINI_PRO_1O5 = genai.GenerativeModel('gemini-1.5-pro')
PATTERN = r'```json\s*({.*?})\s*```'

def get_calories_from_img(image: Image.Image):
    # generate the response 
    response = GEMINI_PRO_1O5.generate_content([
        """give me result by following format
        - meal time must be time format like this "00:00 AM"
        
        ```json{
            "name": str,
            "calories": int,
            "category": str,
            "ingredients": list[str],
            "how_to_cook": str,
            "meal_time": str,
        }```
        
        if the image is not food then return the following format
        ```json{
            "message": "short message"
        }```
        so that i can use in my fastapi 
        response route""",
        image])
    # content_text = response["content"]["parts"][0]["text"]
    content_text = response.text
    print(content_text)
    # Clean the response by removing the JSON-like structure
    match = re.search(PATTERN, content_text, re.DOTALL)
    if match:
        # Extract the JSON string
        json_string = match.group(1)
        # Convert the string to a Python dictionary
        try:
            # Convert the string to a proper Python dictionary
            json_dict = json.loads(json_string)
            return json_dict
        except json.JSONDecodeError as e:
            raise ValueError(f"Failed to decode JSON: {str(e)}")
    else:
        raise ValueError("No JSON-like content found in the response.")
    