import requests
import env

API_KEY = env.OAUTH_API
SEARCH_ENGINE_ID = env.SEARCHE_ENGINE_ID

search_query = "Fried Rice"

url = "https://www.googleapis.com/customsearch/v1"

params = {
    'q': search_query,
    'key': API_KEY,
    'cx': SEARCH_ENGINE_ID
}

response = requests.get(url, params=params)
result = response.json()
print(result)