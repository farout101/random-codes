import requests
import env

def search_google_image(search_query):
    """
    Searches for an image using Google Custom Search API and returns the link to the first image result.

    Parameters:
    search_query (str): The search query string.

    Returns:
    str: The link to the first image result or a message if no results are found.
    """
    API_KEY = env.OAUTH_API
    SEARCH_ENGINE_ID = env.SEARCH_ENGINE_ID

    url = "https://www.googleapis.com/customsearch/v1"

    params = {
        'q': search_query,
        'key': API_KEY,
        'cx': SEARCH_ENGINE_ID,
        'searchType': 'image'
    }

    response = requests.get(url, params=params)
    
    if response.status_code == 200:
        result = response.json()
        if 'items' in result:
            return result['items'][0]['link']
        else:
            return {"error": "didnt find image"}
    else:
        return {"error": "something wrong with image searching server"}
