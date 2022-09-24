import pandas as pd
import openpyxl


## 마트·편의점 : 편의점, 정육점,  슈퍼마켓, 농·축·수산품
## 음식점 : 서양음식, 중국식, 스넥, 일식·횟집, 일반한식, 주점, 칵테일바
## 편의시설 : 병원,치과의원
## 기타 : 헬스클럽, 학원, 제과점, 영화관


list = ["치과의원","농·축·수산품","병원","헬스클럽", "학원", "제과점","영화관","서양음식", "중국식", "스넥", "일식·횟집", "일반한식", "주점","마트·편의점" "편의점" "정육점"  "슈퍼마켓","약국"]

out = pd.DataFrame({
            "업종": 1, "상호명": 1, "시도": 1, "구": 1, "도로명": 1, "주소": 1

    }, index = [0]
)

df = pd.read_excel("16.xlsx", engine = "openpyxl")

for i in range(len(df)):
    for j in list:
        if(j == df.loc[i,'업종']):
            out = out.append(df.loc[i])
            break

out.to_excel("Section_16" + ".xlsx")
