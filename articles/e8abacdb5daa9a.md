---
title: "clojure.stringの使い方の簡単なまとめ"
emoji: "🐈"
type: "tech"
topics:
  - "clojure"
published: true
published_at: "2024-11-01 22:41"
---

Clojureで文字列操するときに使うclojure.stringの、簡単な使い方まとめ。

「ClojureDocsを見ろよ」という話なのだが、主に自分の備忘用にチートシートとしてまとめる。

ClojureDocsのページ
https://clojuredocs.org/clojure.string

## インポート

大前提として、clojure.stringを使用するためにインポートを行う。

```clojure
(require '[clojure.string :as string])
```

## blank?

空文字であるかを判定する。

```clojure
(string/blank? "")
;; true
(string/blank? "  ")
;; true
(string/blank? "hello")
;; false
(string/blank? nil)
;; false
```

## capitalize

最初の文字を大文字にしてくれる。

```clojure
(string/capitalize "hello world")
;; Hello world

(string/capitalize "HELLO WORLD")
;; Hello world

(string/capitalize "clojure")
;; Clojure
```

## ends-with?

文字列が特定の文字列で終わるかどうかを判定する関数。

```clojure
(string/ends-with? "hello world" "world")
;; true
(string/ends-with? "hello world" "hello")
;; false
(string/ends-with? "clojure" "ure")
;; true
(string/ends-with? "clojure" "clo")
;; false
```

## escape

文字列の中の特定の文字をエスケープしたいとき。

```clojure
(def html-entities {\& "&ampl" \< "&lt;" \> "&gt;" \" "&quot;"})

(string/escape "This & that < those > these" html-entities)
```
      
## include?

ある文字列が別の文字列に含まれているかどうかを判定する。

```clojure
(string/includes? "hello world" "world")
;; true
(string/includes? "hello world" "clojure")
;; false
```

## index-of

ある文字列が対象の文字列に含まれているかどうかを判定し、その位置を返す。

```clojure
(string/index-of "hello world" "world")
;; 6
(string/index-of "hello world" "h")
;; 0
(string/index-of "hello world" "l")
;; 2
(string/index-of "hello world" "clojure")
;; nil
```

## join

複数の文字列やシーケンスを特定の区切り文字で結合する。

```clojure
(string/join ["Hello" "world"])
;; "Helloworld"
(string/join ", " ["Hello" "world"])
;; "Hello, world"
(string/join "-" [1 2 3 4 5])
;; "1-2-3-4-5"
```

## last-index-of

指定された文字列が対象の文字列に最後に出現する位置を返す。

```clojure
(string/last-index-of "hello world hello" "hello")
;; 12
(string/last-index-of "hello world hello" "hello" 10)
;; 0
```

## lower-case

文字列のすべての文字を小文字に変換する。

```clojure
(string/lower-case "Hello World!")
;; "hello world!"
(string/lower-case "CLOJURE")
;; "clojure"
```

## re-quote-replacement

文字列置換の際に特殊文字をエスケープする。
例えば、正規表現に`$`が含まれていた場合は文末として扱われるが、そのままの文字で扱いたいときは、この関数を用いてエスケープする。
例えば、文末出ない位置にある`$name`を、`$USER`に変えたいときなど。

使い方がわかりにくかったので、構文を追加している。

```clojure
;; 構文
;; (string/replace "置換対象の文字列" 正規表現 (string/re-quote-replacement "置換文字列"))

(string/replace "Hello $name, welcome!" #"\$name" (string/re-quote-replacement "$USER"))
;; => "Hello $USER, welcome!"
```

## replace

対象文字列の特定の文字列、または、正規表現パターンに一致する部分を置換する。

```clojure
(string/replace "hello world" "world" "Clojure")
;; "Hello Clojure"
(string/replace "hello 123 world" #"\d+" "numbers")
;; "hello numbers world"
```

## replace-first

文字列の中の特定の文字列、または、正規表現パターンに一致する最初の出現部分のみを置換する。
ちょっとわかりにくかったので、構文も書いている。

```clojure
;; 構文
;; (clojure.string/replace-first target pattern replacement)

(string/replace-first "hello world hello" "hello" "hi")
;; "hi world hello"
(string/replace-first "hello 123 world 456" #"\d+" "numbers")
;; "hello numbers world 456"
```

## reverse

文字列を逆順に並べ替える。

```clojure
(string/reverse "hello")
;; "olleh"
(string/reverse "Clojure")
;; "erujolC"
```

## split

区切り文字や正規表現で文字列を分割。

```clojure
(string/split "apple,orange,banana" #",")
;; ["apple" "orange" "banana"]

(string/split "hello world clojure" #"\s")
;;["hello" "world" "clojure"]

;; どこまで分割するかの数を指定できる
(string/split "apple,orange,banana" #"," 2)
;; ["apple" "orange,banana"]
```

## split-lines

文字列を行ごとに分割する。
改行文字によって分割される。

```clojure
(string/split-lines "Hello\nworld\nClojure")
;; ["Hello" "world" "Clojure"]
```

## starts-with?

文字列が特定の文字列で始まっているかを判定する。

```clojure
(string/starts-with? "hello world" "hello")
;; true
(string/starts-with? "hello world" "world")
;; false
```

## trim

文字列の前後にある空白（スペース、タブ、改行など）を取り除く。

```clojure
(string/trim "   hello world    ")
;; "hello world"
(string/trim "\n\tClojure\n\t")
;; #Clojure
```

## trim-newline

文字列の末尾にある改行文字を取り除く。

```clojure
(string/trim-newline "hello world\n")
;; "hello world"
(string/trim-newline "hello world\r\n")
;; "hello world"
(string/trim-newline "hello\nworld\n")
;; "hello\nworld"
```

## triml

文字列の戦闘にある空白（スペース、タブ、改行など）を取り除く。

```clojure
(string/triml "   hello world    ")
;; "hello world    "
(string/triml "\t\n Clojure\n")
;; "Clojure\n"
```

## trimr

文字列の末尾にある空白（スペース、タブ、改行など）を取り除く。

```clojure
(string/trimr "   hello world    ")
;; "   hello world"
(string/trimr "Clojure\n\t ")
;; "Clojure"
```


