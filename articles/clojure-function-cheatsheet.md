---
title: "Clojureの関数・マクロの自分用チートシート"
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

## last

`(last coll)`

シーケンス(リスト、ベクタなど)の最後の要素を取得する。

https://clojuredocs.org/clojure.core/last

```clojure
(last [1 2 3 4])
;; 4
(last '(10 20 30))
;; 30
(last ["c" "l" "o" "j" "u" "r" "e"])
;; "e"
(last [])
;; nil
(last '())
;; nil
```

## cons

`(cons x seq)`

シーケンスの先頭に要素を追加した、新しいリストを返す。
なお、`nil`はシーケンスとして扱われる。

https://clojuredocs.org/clojure.core/cons

```clojure
(cons 1 [2 3 4])
;; (1 2 3 4)
(cons "a" '("b" "c"))
;; ("a" "b" "c")

;; nilをシーケンスとして扱う
(cons 1 nil)
;; (1)
(cons "a" nil)
;; ("a")
```

## rest

`(rest coll)`

シーケンスの最初の要素を取り除いた、残りの要素を返却する。
新しいリストを返すのではなく、元のシーケンスから先頭の要素を除いた遅延シーケンスを返す。

https://clojuredocs.org/clojure.core/rest

```clojure
(rest [1 2 3 4])
;; (2 3 4)
(rest '(10 20 30 40))
;; (20 30 40)
(rest [])
;; ()
```

新しいリストを返したい場合は明示的に返す方法を取る。

```clojure
(vec (rest [1 2 3 4 5]))
;; [2 3 4 5]
(list* (rest '(1 2 3 4 5))) ;-> 遅延シーケンスなのでlist*を使用する。
;; (2 3 4 5)
```

#### nextとrestの違い

空シーケンス、もしくは要素一つのシーケンスを渡した際に違いがある。
`next`は`nil`を返す。
`rest`は`()`を返す。

```clojure
(next [])
;; nil
(rest [])
;; ()

(next [1])
;; nil
(rest [1])
;; ()
```

## list*

`(list* args)`
`(list* a args)`
`(list* a b args)`
`(list* a b c args)`
`(list* a b c d & more)`

シーケンス全体をリストとしてまとめるために使用する。
特に遅延シーケンスをリストとして評価する場合に使用する。

`list`がシーケンスを一つとしてまとめた扱う場合に、`list*`がシーケンスの各要素を展開してリスト化する場合に使用する。

https://clojuredocs.org/clojure.core/list*

```clojure
(list data)
;; ((1 2 3))
(list* data)
;; (1 2 3)

(list 0 data)
;; (0 (1 2 3))
(list* 0 data)
;; (0 1 2 3)

(list (rest data))
;; ((2 3))
(list* (rest data))
;; (2 3)
```

## into

`(into)`
`(into to)`
`(into to from)`
`(into to xform from)`

コレクションを別のコレクションに変換してから結合する。
第一引数である、`to`のコレクションの形式に変換される。

https://clojuredocs.org/clojure.core/into

```clojure
(into [1 2 3] [4 5 6])
;; [1 2 3 4 5 6]

(into [] '(1 2 3 4))
;; [1 2 3 4]

(into #{} [1 2 3 4])
;; #{1 4 3 2}

(into {:a 1} [[:b 2] [:c 3]])
;; {:a 1, :b 2, :c 3}
```

and more...
