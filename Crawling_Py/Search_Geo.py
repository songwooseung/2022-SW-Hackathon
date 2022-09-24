import requests, json
import pandas as pd

api_key = "acc529e3741f521c3551460ca6e8fecf"

out = pd.DataFrame({
            "x": 1, "y": 1,
    }, index = [0]
)

def addr_to_lat_lon(addr):
    url = 'https://dapi.kakao.com/v2/local/search/address.json?query={address}'.format(address=addr)
    headers = {"Authorization": "KakaoAK " + api_key}
    result = json.loads(str(requests.get(url, headers=headers).text))
    match_first = result['documents'][0]['address']
    return float(match_first['x']), float(match_first['y'])

df = pd.read_excel("Section.xlsx")

adr = ""
geo = ""
for i in range(len(df)-1):
    adr = str(df.loc[i+1,'시도']) + " " + str(df.loc[i+1,'구']) + " " + str(df.loc[i+1,'도로명']) + " " + str(df.loc[i+1,'주소'])
    try:
         geo = addr_to_lat_lon(adr)

         out_temp = pd.DataFrame({ "x": [str(geo[0])], "y": [str(geo[1])]
         })

         out = pd.concat([out, out_temp], ignore_index=True)
         print(i)
    except:
        out_temp = pd.DataFrame({
            "x": [""], "y": [""]
        })

        out = pd.concat([out, out_temp], ignore_index=True)
        print(i)
        continue
df = df.reset_index()
out = out.reset_index()
df = pd.concat([df,out], axis=1)


df.to_excel("Geo.xlsx")