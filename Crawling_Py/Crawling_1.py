import requests
import pandas as pd
from bs4 import BeautifulSoup
import openpyxl


out = pd.DataFrame({
            "업종": 1, "상호명": 1, "사업자번호": 1, "시도": 1, "구": 1, "도로명": 1, "주소": 1
    }, index = [0]
)

index_cnt = 1

cookies = {
    'SITE_CK': 'OK',
    'PHPSESSID': 'hec1e9uq2cpfcee6ld4hrbnfv0',
    '_ga': 'GA1.2.1770714465.1663733541',
    '_gid': 'GA1.2.245481894.1663733541',
    '_gat_gtag_UA_168139343_1': '1',
    'c_layopen': 'ok',
}
headers = {
    'Host': 'www.xn--2e0bu9hsujd1k8xofxc.kr',
    'Upgrade-Insecure-Requests': '1',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.5195.102 Safari/537.36',
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
    'Referer': 'http://www.xn--2e0bu9hsujd1k8xofxc.kr/',
    # 'Accept-Encoding': 'gzip, deflate',
    'Accept-Language': 'ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7',
    'Connection': 'close',
    # Requests sorts cookies= alphabetically
    # 'Cookie': 'SITE_CK=OK; PHPSESSID=hec1e9uq2cpfcee6ld4hrbnfv0; _ga=GA1.2.1770714465.1663733541; _gid=GA1.2.245481894.1663733541; _gat_gtag_UA_168139343_1=1; c_layopen=ok',
}

for i in range(1, 25128):
    params = {
        'sel_search': "1",
        'word': '',
        'page': str(i)
    }
    response = requests.get('http://www.xn--2e0bu9hsujd1k8xofxc.kr/iframe_search.php', params=params, cookies=cookies, headers=headers, verify=False)
    soup = BeautifulSoup(response.text,"html.parser")
    items = soup.findAll({"td"})
    print(i)
    Type = ""
    name = ""
    number = ""
    adr_1 = ""
    adr_2 = ""
    adr_3 = ""
    adr_4 = ""
    k = 1
    for j in items:
        if( k % 4 ) == 1: Type = j.text
        elif( k % 4 )== 2: name = j.text
        elif( k % 4 ) ==  3: number = j.text
        else:
            try:
                adresslist_1 = j.text.split('구', 2)
                adr_2 = (adresslist_1[1] + "구")
                adr_1 = (adresslist_1[0] + "구")
                if( adr_2 != "북구"):
                    k = 1
                    continue


                adresslist_1[2] = adresslist_1[2].split("길",1)

                if(len(adresslist_1[2]) == 1):
                    adresslist_1[2] = adresslist_1[2][0].split("로", 1)
                    if (len(adresslist_1[2]) == 1):
                        adresslist_1[2] = adresslist_1[2][0].split("동", 1)
                        adr_4 = (adresslist_1[2][1])
                        adr_3 = (adresslist_1[2][0] + "동")
                    else:
                        adr_4 = (adresslist_1[2][1])
                        adr_3 = (adresslist_1[2][0] + "로")
                else:
                    adr_4 = (adresslist_1[2][1])
                    adr_3 = (adresslist_1[2][0] + "길")

                out_temp = pd.DataFrame({
                    "업종": [Type], "상호명": [name] ,"사업자번호": [number], "시도": [adr_1], "구": [adr_2], "도로명": [adr_3], "주소": [adr_4]
                })
                out = pd.concat([out,out_temp],ignore_index= True)
                k = 0

            except:
                k = 0
        k = k + 1
    if len(out) > 5000:
        out.to_excel(str(index_cnt) + ".xlsx")

        lenth = len(out)
        for l in range(lenth - 1):
            out.drop(index = lenth - 1-l, inplace = True)
        index_cnt = index_cnt + 1
        index_cnt + 1

out.to_excel(str(index_cnt) + ".xlsx")