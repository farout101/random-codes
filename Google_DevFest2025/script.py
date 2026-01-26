import pprint
import google.generativeai as genai
import json
from model import load_creds

creds = load_creds()

genai.configure(credentials=creds)
# print()
# print('Available base models:', [m.name for m in genai.list_models()])


def generate_food_suggestion(user_info: str):
    try:
        model = genai.GenerativeModel(model_name='tunedModels/food-suggestion-ai-v1-uss801z982xp')
        result = model.generate_content(user_info)
        print(result.text)
        response = json.loads(result.text)
        return response
    
    except json.JSONDecoder as json_err:
        pass

    except Exception as e:
        pass

    return None



print(generate_food_suggestion(
    """{
  "weight": 60,
  "height": 165,
  "age": 25,
  "diseases": ["None"],
  "allergies": ["Peanuts"],
  "gender": "Female",
  "exercise": "High"
}"""
))