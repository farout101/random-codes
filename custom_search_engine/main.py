import requests
import env

API_KEY = env.OAUTH_API
SEARCH_ENGINE_ID = env.SEARCHE_ENGINE_ID

search_query = "Beef ribs"

url = "https://www.googleapis.com/customsearch/v1"

params = {
    'q': search_query,
    'key': API_KEY,
    'cx': SEARCH_ENGINE_ID,
    'searchType': 'image'
}

response = requests.get(url, params=params)
result = response.json()
# print(result)

print("\n------The Below is the link to the image------\n")
if 'items' in result:
    print(result['items'][0]['link'])