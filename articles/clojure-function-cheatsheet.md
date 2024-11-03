---
title: "Clojureで知った関数・マクロをまとめる"
emoji: "🐈"
type: "tech"
topics: ["clojure"]
published: true
---

Clojureで知った関数・マクロをまとめる。
追記してどんどん増やしていくので、ぼちぼち内容が増えていくと思う。

## defn-

`(defn- name & decls)`
`name`: 関数名
`& decls`: 関数定義用の可変引数

プライベートな関数を定義するマクロ。
プライベートとは、同じネームスペース内でしか使えないことを指す。

```clojure
(defn- private-function
  [x]
  (* x x))

;; :privateメタデータがtrueになっているのが確認できる。
(meta #'private-function)
;; {:private true,
;;  :arglists ([x]),
;;  :line 413,
;;  :column 1,
;;  :file "/Users/hello-clojure/src/hello_clojure/core.clj",
;;  :name private-function,
;;  :ns #namespace[hello-clojure.core]}

(defn public-function
  [x]
  (* x x))

;; :privateのメタデータが無いことが確認できる。
(meta #'public-function)
;; {:arglists ([x]),
;;  :line 426,
;;  :column 1,
;;  :file "/Users/hello-clojure/src/hello_clojure/core.clj",
;;  :name public-function,
;;  :ns #namespace[hello-clojure.core]}
```

https://clojuredocs.org/clojure.core/defn-

## meta

`(meta obj)`
メタデータを取得するための関数。

```clojure
(def ^:private myvar 42)
(defn ^:deprecated myfn [] "Hello")

(meta #'myvar)
;; {:private true,
;;  :line 438,
;;  :column 1,
;;  :file "/Users/hello-clojure/src/hello_clojure/core.clj",
;;  :name myvar,
;;  :ns #namespace[hello-clojure.core]}

(meta #'myfn)
;; {:deprecated true,
;;  :arglists ([]),
;;  :line 439,
;;  :column 1,
;;  :file "/Users/hello-clojure/src/hello_clojure/core.clj",
;;  :name myfn,
;;  :ns #namespace[hello-clojure.core]}
```

なお、メタデータ付与のための記法として、以下のようなものがある。

`^:keyword`
`^{:key1 val1, key2 val2}`

メタデータ取得のための記法として、以下の記法がある。

`#'obj`

```clojure
;; 何もつけないと値として評価される
myvar
;; 42

;; #'をつけると、オブジェクトそのものが返される
#'myvar
;; #'hello-clojure.core/myvar
```

https://clojuredocs.org/clojure.core/meta

## threading-macro

`->`

メソッドチェーンのようなことができるマクロ。
前のメソッドの処理結果は、次のメソッドの第一引数に渡されるようだ。

https://clojure.org/guides/threading_macros

```clojure
(def text " hello world ")

;; threading macroを使う場合
(-> text
    clojure.string/trim
    clojure.string/upper-case
    (str "!!!"))
;; "HELLO WORLD!!!" ;-> 第一引数に渡されているのがわかる

;; threading macroを使わない場合
(str (clojure.string/upper-case (clojure.string/trim text)) "!!!")
;; "HELLO WORLD!!!"
```

## threading-last-macro

`->>`

メソッドチェーンのようなことができるマクロ。
前のメソッドの処理結果は、次のメソッドの末尾の引数に渡されるようだ。

https://clojure.org/guides/threading_macros

```clojure
(def person {:name "Alice" :age 28 :city "New York"})

(str "Age in 10 years: " (+ 10 (:age person)))
;; "Age in 10 years: 38" 

;; threading macroでの例
(-> person
    (:age)
    (+ 10)
    (str "Age in 10 years: "))
;; "38Age in 10 years: "

;; threading last macroでの例
(->> person
     (:age)
     (+ 10)
     (str "Age in 10 years: "))
;; "Age in 10 years: 38"

```

and more...