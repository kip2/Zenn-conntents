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

ベクタとリストで追加される位置が異なることに注意する。
特に、リストは取り出した順番に先頭に追加していっている模様(片側リストだからだと思う)。

```clojure
;; ベクタは末尾に追加する
(into [1 2 3] [:a :b :c])
;; [1 2 3 :a :b :c]

;; リストは先頭に追加する
(into '(1 2 3) '(:a :b :c))
;; (:c :b :a 1 2 3)
```

## defonce

`(defonce name expr)`

グローバル変数を一度だけ評価するためのマクロ。
すでにその変数が存在している場合は再評価されない。

https://clojuredocs.org/clojure.core/defonce

```clojure
;; DBコネクションの例
(defonce db-connection (initialize-db))

;; コンフィグなど
(defonce config {:db "localhost" :port 54321})
```

別の例。
カウンターをインクリメントし、その後にもう一度`(defonce counter (atom 0))`を実行しても、再評価されないので、カウンターの初期化が防げる。

```clojure
(defonce counter (atom 0))

(defn increment-counter []
    (swap! counter inc))
```

## atom

`(atom x)`
`(atom x & options)`

スレッドセーフでミュータブルを扱うための仕組み。
複数のスレッドからのアクセスがあっても、安全に変更できる。
ここではあくまで使い方について記載しているので、仕組みそのものは別記事などを見てほしい。

https://clojuredocs.org/clojure.core/atom


```clojure
;; 基本構文
(def myatom (atom initial-avlue))
;; myatomにatomを格納
;; initial-valueが初期値
```

値の取得

```clojure
;; 現在の値を取得
@myatom
;; derefでも同様に取得可能
(deref myatom)
```

値の更新

```clojure
;; 現在の値をインクリメント
(swap! myatom inc)
;; 値を100にリセット
(reset! myatom 100)
```

参考
https://japan-clojurians.github.io/clojure-site-ja/reference/atoms

## use-fixtures

clojure.testが提供するマクロ。
テストの前後処理を定義する。

https://clojuredocs.org/clojure.test/use-fixtures

```clojure
;; clojure.testを使うので、インポート
(:require [clojure.test :refer :all])

;; :onceでテスト全体で一度だけ実行
(use-fixtures :once
  (fn [tests]
    ;; 前処理。サーバーの起動
    (start-server)
    (try
      ;; テストの実行
      (tests)
      ;; 後処理。サーバーの停止
      (finally
        (stop-server)))))
```

## slurp

`(slurp f & opts)`

ファイルやURLやストリームなどのリソースから、内容を一度にすべて読み込んで文字列を返す。
小さいファイルなど、扱う容量が小さい場合に使用する。

https://clojuredocs.org/clojure.core/slurp

```clojure
;; ファイルからの読み込み
(slurp "path/file.txt")

;; URLからの読みこみ
(slurp "https://example.com")

;; ストリームからの読み込み
(with-open [rdr (java.io.InputStreamReader. (java.io.FileInputStream. "read-file.txt"))]
    (slurp rdr))
```

## get-in

`(get-in m ks)`
`(get-in m ks not-found)`

`m`: 取り出し元となるデータ構造。
`ks`: 値を取り出すための階層。キーのリストとして渡す。
`not-found`: 指定したパスが存在しない場合に返すデフォルト値。

ネストされたデータ構造(マップやベクター)の中から特定の値を取得する。

https://clojuredocs.org/clojure.core/get-in


例として、以下のようなネスト構造のデータがあるとする。

```clojure
(def person {:name "Alice"
            :address {:city "Wonderland"
                        :postal-code "12345"}})
```

```clojure
;; :addressの:cityを取り出す場合
(get-in person [:address :city])
;; "Wonderland"

;; 値がない場合のデフォルト値
(get-in person [:address :country] "Unknown")
;; "Unknown"
```

なお、get-inを使わない場合は以下のようになる。

```clojure
(get (get (:address person) :city) nil)
```

## prn

`(prn & more)`

値をコンソールに出力する。
主に、デバッグ時にClojureのデータ構造を確認するために使う。

https://clojuredocs.org/clojure.core/prn

```clojure
(prn "Hello, Clojure!")
;; "Hello, Clojure!"
```

printlnとの違いは

- エスケープシーケンスがそのまま出力されること。
- Clojureのデータ構造をそのまま表せること。

```clojure
;; エスケープシーケンスが出力される。
(prn "Hello, \"Clojure\"")
;; "Hello, \"Clojure\""

;; エスケープシーケンスが出力されない。
(println "Hello, \"Clojure\"")
;; Hello, "Clojure"


(def a-map {:a 1 :b "hello" :c [1 2 3]})

;; 文字列にダブルクォートがついた状態で出力される
(prn a-map)
;; {:a 1, :b "hello", :c [1 2 3]}
;; 文字列にダブルクォートがない状態で出力される
(println a-map)
;; {:a 1, :b hello, :c [1 2 3]}
```

## sort

`(sort coll)`
`(sort comp coll)`

コレクションの要素を並び替えるための関数。
デフォルトでは昇順だが、カスタム関数を指定することで順序は変更できる。

https://clojuredocs.org/clojure.core/sort

```clojure
(sort [3 1 4 1 5 8 9 2 7 3])
;; (1 1 2 3 3 4 5 7 8 9)

(sort ["apple" "orange" "banana" "grape"])
;; ("apple" "banana" "grape" "orange")

(sort > [3 1 4 1 5 8 9 2 7 3])
;; (9 8 7 5 4 3 3 2 1 1)
(sort #(> (count %1) (count %2)) ["apple" "orange" "banana" "grape"])
;; ("orange" "banana" "apple" "grape")
```

## assoc

`(assoc map key val)`
`(assoc map key val & kvs)`

マップやベクターに新しいキーと値を追加したり、既存のキーの値を変更する。

https://clojuredocs.org/clojure.core/assoc


```clojure
;; リストの場合
(def a-map {:a 1 :b 2})

;; 新しいキーの追加
(assoc a-map :c 3)
;; {:a 1, :b 2, :c 3}

;; 既存のキーの変更
(assoc a-map :a 10)
;; {:a 10, :b 2}

;; 複数のキーと値の追加と更新
(assoc a-map :a 10 :c 3 :d 4)
;; {:a 10, :b 2, :c 3, :d 4}

;; ベクターの場合
(def v-map [1 2 3])

;; 値の更新
(assoc v-map 1 20)
;; [1 20 3]

;; 範囲外はアクセスできない
(assoc v-map 99 30)
;; Execution error (IndexOutOfBoundsException) 
```

## map

`(map f)`
`(map f coll)`
`(map f c1 c2)`
`(map f c1 c2 c3)`
`(map f c1 c2 c3 & colls)`

リストやベクタなどのシーケンスの各要素に関数を適用し、新しいリストを返す関数。

https://clojuredocs.org/clojure.core/map

```clojure
(map #(* 2 %) [1 2 3 4 5])
;; (2 4 6 8 10)

;; 複数のシーケンス
(map + [1 2 3] [4 5 6])
;; (5 7 9)

;; 無限のシーケンスにも使用できる
;; 無限のシーケンスの場合はtakeなどで取り出す関数を指定しないと無限に取り出されてしまう。
;; (map #(* 2 %) (range))
;; この場合は(0 1 2 ... 97 98 ...)という感じで表示された。
(take 5 (map #(* 2 %) (range)))
;; (0 2 4 6 8)
```

## mapv

`(map f)`
`(map f coll)`
`(map f c1 c2)`
`(map f c1 c2 c3)`
`(map f c1 c2 c3 & colls)`

リストやベクタなどのシーケンスの各要素に関数を適用し、新しいベクタを返す関数。
mapと同機能だが、ベクタを返すところが違う。

https://clojuredocs.org/clojure.core/mapv

```clojure
(mapv inc [1 2 3 4 5])
;; [2 3 4 5 6]

(mapv + [1 2 3] [4 5 6])
;; [5 7 9]

;; mapvは即時評価なので、無限シーケンスは扱えない。
;; (take 5 (mapv #(* 2 %) (range)))
```

## filter

`(filter pred)`
`(filter pred coll)`

リストやベクタなどのシーケンスから、特定の条件に一致する要素だけを抽出する。

https://clojuredocs.org/clojure.core/filter

```clojure
(filter even? [1 2 3 4 5 6])
;; (2 4 6)

(filter #(< % 3) #{1 2 3 4 5})
;; (1 2)

(filter #(= \a (first %)) ["apple" "banana" "cherry" "apricot"])
;; ("apple" "apricot")

;; 無限シーケンスにも使用できる
(take 3 (filter even? (range)))
;; (0 2 4)
```

## every?

`(every? pred coll)`

リストやベクタなどのコレクション内のすべての要素が指定した条件を満たすかどうかをチェックする

https://clojuredocs.org/clojure.core/every_q

```clojure
(every? even? [2 4 6 8])
;; true
(every? even? [1 3 5 7])
;; false
(every? even? [1 2 3 4 5 6])
;; false

(every? string? ["apple" "orange" "banana"])
;; true

;; 空のコレクションはtrueと評価されることに注意する。
(every? even? [])
;; true
```

---

and more...
