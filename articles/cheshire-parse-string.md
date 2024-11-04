---
title: "cheshireでJSONをパースするときにハマったところ"
emoji: "🐈"
type: "tech"
topics: ["clojure"]
published: true
---

# cheshireでJSONをパースするときにハマったところ

cheshireのparse-stringでハマったところがあったので、備忘として。

https://github.com/dakrone/cheshire

## なにがおきたか

Ringのハンドラをテストするコードを書いていたが、テストが失敗する。

```clojure
(:require [cheshire.core :as json])

(deftest test-handler-json
  (testing "GET /json"
    (let [response (app (mock/request :get "/json"))
          body (json/parse-string (bs/to-string (:body response)) true)]
      (is (= 200 (:status response)))
      (is (= "application/json;charset=UTF-8" (get-in response [:headers "Content-Type"])))
      (is (= {:message "これはJSONレスポンスです" :status "success"} body)))))
```

テスト結果。

```clojure
; ; expected:
; {:message "これはJSONレスポンスです", :status "success"}
; 
; ; actual:
; {"message" "これはJSONレスポンスです", "status" "success"}
```

キーが文字列として扱われている。

## なぜそうなったのか

これは、JSONで扱っている文字列そのままで、キーがパースされるからのようだ。

参考として、JSONではこのようなイメージ。

```json
{
    "message": "これはJSONレスポンスです",
    "status": "success"
}
```

## オプションでtrueを指定すると回避できる

parse-stringにtrueを指定すると、Clojureのマップで扱うキーワードに変換してくれる。

```clojure
(def json-string "{\"message\": \"これはJSONレスポンスです\", \"status\": \"success\"}")

;; trueを指定せずパースする
(def parsed-without-true (json/parse-string json-string))

;; trueを指定してパースする
(def parsed-true (json/parse-string json-string true))

;; true指定なしの出力
(prn parsed-without-true)
;; => {"message" "これはJSONレスポンスです", "status" "success"}

;; true指定の出力
(prn parsed-true)
;; => {:message "これはJSONレスポンスです", :status "success"}
```
