---
title: "Clojureの関数・マクロの自分用チートシート"
emoji: "🐈"
type: "tech"
topics: ["clojure"]
published: true
---

Clojureで知った関数・マクロをまとめる。
追記してどんどん増やしていくので、ぼちぼち内容が増えていくと思う。

なお、記事の項目はアルファベット順になっている。


## abs

`(abs a)`

絶対値を取得する関数。

https://clojuredocs.org/clojure.core/abs

```clojure
(abs -10)
;; 10

(abs 5)
;; 5

(abs 0)
;; 0

(abs nil)
;; NullPointerException
```

## apropos

`(apropos str-or-pattern)`

指定したキーワードに基づいて、現在の環境内でそのキーワードにマッチするシンボルを検索する関数。
要するに、同じプロジェクトやファイル内の関数や変数などを検索する機能。

https://clojuredocs.org/clojure.repl/apropos

```clojure
(require '[clojure.repl :refer [apropos]])

(apropos "reduce")
;;  (clojure.core/ensure-reduced
;;  clojure.core/reduce
;;  clojure.core/reduce-kv
;;  clojure.core/reduced
;;  clojure.core/reduced?
;;  clojure.core/stream-reduce!
;;  clojure.core/unreduced
;;  clojure.core.async/reduce)

;; 同環境内の関数を検索
(defn greet
  "takes a name retruens a greeting string."
   [name]
   (str "Hello, " name "!"))

(apropos "greet")
;; (user/greet)
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


## comp

`(comp)`
`(comp f)`
`(comp f g)`
`(comp f g & fs)`

関数を合成するための関数。
複数の関数を組み合わせて一つの関数を作成する。
合成の順番は右から左。

https://clojuredocs.org/clojure.core/comp

```clojure
;; 2倍する関数
(def multiply-by-2 (fn [x] (* x 2)))

;; 1足す関数
(def add-one (fn [x] (+ x 1)))

;; 関数合成。2倍して1足す関数。
(def double-then-add-one (comp add-one multiply-by-2))

(double-then-add-one 5)
;; 11

;; 大文字にする関数をバインド
(def to-uppercase clojure.string/upper-case)

;; 最初の5つの要素を取り出す関数
(def take-first-5 #(subs % 0 5))

;; 関数合成。大文字にしてから、最初から5つの要素を取りだす。
(def uppercase-and-take-first-5 (comp take-first-5 to-uppercase))

(uppercase-and-take-first-5 "clojure")
;; "CLOJU"
```

## compare

`(compare x y)`

2つの値を比較する。

https://clojuredocs.org/clojure.core/compare

```clojure

(compare 3 5)
;; -1 (3は5より小さい)

(compare 5 5)
;; 0 (5と5は等しい)

(compare 7 5)
;; 1 (7は5よりおおきい)

;; 他の型
(compare "apple" "banana")
;; -1 (辞書順でappleはbananaより前)

(compare :a :b)
;; -1 (キーワード順で、:aは:bより前)

(compare [1 2 3] [1 2 4])
;; -1 (リストの順序で、3は4より前)

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

## defmacro

`(defmacro name doc-string? attr-map? [params*] body)`
`(defmacro name doc-string? attr-map? ([params*] body) + attr-map?)`

マクロを定義するための構文。
マクロは、実行時ではなく、コンパイル時にコードが生成される機能。
煩雑な記法を簡略化する場合などに使われる。

https://clojuredocs.org/clojure.core/defmacro

```clojure
(defmacro my-when [condition & body]
  `(if ~condition
     (do ~@body)))

(my-when true
    (println "This will be printed.")
    (println "So will this."))
;; This will be printed.
;; So will this.
```

## defn-

`(defn- name & decls)`
`name`: 関数名
`& decls`: 関数定義用の可変引数

プライベートな関数を定義するマクロ。
プライベートとは、同じネームスペース内でしか使えないことを指す。

https://clojuredocs.org/clojure.core/defn-

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

## defprotocol

`(defprotocol name & opts+sigs)`

インターフェース(プロトコル)を定義するための構文。
メソッドを定義し、データ型に実装するための仕組みを提供する。
Rustで言うところの`trait`のような機能と思われる。

https://clojuredocs.org/clojure.core/defprotocol

```clojure
;; プロトコルの定義
(defprotocol Greetable
  (greet [this] "Returns a greeting message"))

;; プロトコルの実装
(defrecord Person [name]
  Greetable
  (greet [this] (str "Hello, my name is " name "!")))

(def john (->Person "John"))

;; Personレコードに実装されたgreetを使用するする。
(greet john)
;; "Hello, my name is John!"

;; 別の型に対しても同様に定義する
(defrecord Robot [id]
  Greetable
  (greet [this] (str "Beep boop, I am robot #" id ".")))

(def robo (->Robot "robo"))

(greet robo)
;; "Beep boop, I am robot #robo."

```

## defrecord

`(defrecord name [& fields] & opts+specs)`

レコードを定義するための構文。
レコードとは、特定のフィールドを持つデータ構造。
主に「値オブジェクト」の用途で使われる。

https://clojuredocs.org/clojure.core/defrecord

```clojure
;;  Personという新しいデータ方を作成
(defrecord Person [name age])

;; Personレコードのインスタンスとしてjohnを作成
(def john (->Person "John" 30))

;; フィールドへのアクセス
(:name john)
;; John
(:age john)
;; 30
```

## do

複数の式を順番に評価し、最後の式の結果を返す。
主に副作用主体のコードを書く場合に使う。

https://clojuredocs.org/clojure.core/do

```clojure
(do
  (println "first process")
  (println "secod process")
  42)
; first expression
; secod expression
;; 42 <= 最後に評価された式

;; if文などで複数の結果を処理したい場合などに使用する。
(if true
  (do
    (println "条件が真の場合の処理1")
    (println "条件が真の場合の処理2")
    true)
  false)
; 条件が真の場合の処理1
; 条件が真の場合の処理2
;;true
```
## doall

`(doall coll)`
`(doall n coll)`

遅延シーケンスを完全に評価するための関数。
ファイル読み込みなどでは、遅延評価されると内容が変わる可能性があり、それを避けるために使うことがあるらしい。

https://clojuredocs.org/clojure.core/doall

```clojure
(doall (map println [1 2 3 4]))
; 1
; 2
; 3
; 4
;; (nil nil nil nil)

;; ファイルを一度に善行読みこみ、ファイルが閉じられても参照できるようにする
(with-open [rdr (clojure.java.io/reader "file.txt")]
(doall (line-seq rdr)))
```

## doc

`(doc name)`

関数や変数、マクロなどのドキュメント情報を表示する。
通常、REPL上で関数やマクロを調べるのに使用する。

https://clojuredocs.org/clojure.repl/doc

```clojure
(doc str)

;; ユーザー定義の関数でも使用可能
(defn greet
  "takes a name retruens a greeting string."
   [name]
   (str "Hello, " name "!"))

(doc greet)
; -------------------------
; user/greet
; ([name])
;   takes a name retruens a greeting string.

```

## doseq

`(doseq seq-exprs & body)`

反復処理をするマクロ。
各アイテムに対して、副作用を伴う操作を行うために使う。

https://clojuredocs.org/clojure.core/doseq

```clojure
(doseq [i (range 5)]
  (println "Number:" i))
; Number: 0
; Number: 1
; Number: 2
; Number: 3
; Number: 4)

;; 複数のコレクションに対しても同様に実行できる。
(doseq [x [1 2 3]
        y ["a" "b" "c"]]
  (println x y))
; 1 a
; 1 b
; 1 c
; 2 a
; 2 b
; 2 c
; 3 a
; 3 b
; 3 c
```

## drop

`(drop n)`
`(drop n coll)`

シーケンスから指定した数の要素をとばして、残りのシーケンスを返す関数。

https://clojuredocs.org/clojure.core/drop

```clojure
(drop 1 '(1 2 3 4 5))
;; (2 3 4 5)

(drop 1 [1 2 3 4 5])
;; (2 3 4 5)

;; ドロップ指定数がコレクションの長さを超える場合
(drop 10 [1 2 3])
;; ()
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

## find-doc

`(find-doc re-string-or-pattern)`

Clojureのドキュメント検索に使用する関数。
指定したキーワードを含むドキュメントを持つ関数やマクロを検索できる。
通常、REPL上で関数やマクロを調べるのに使用する。

https://clojuredocs.org/clojure.repl/find-doc

```clojure
;; import
(require '[clojure.repl :refer [find-doc]])

(find-doc "reduce")
```

## flatten

`(flatten x)`

ネストされたコレクションを1次元に変換する関数。

https://clojuredocs.org/clojure.core/flatten

```clojure
(flatten [1 [2 3] [4 [5 6]]])
;; (1 2 3 4 5 6)
```

## floor

`(floor a)`

指定した数値を切り捨てて最大の整数に変換する。
要するに小数点以下を切り捨てる。

https://clojuredocs.org/clojure.math/floor

```clojure
(Math/floor 4.2)
;; 4.0

;; 整数への変換
(int (Math/floor 4.2))
;; 4
```

## for

`(for seq-exprs body-expr)`

リスト内包を行うマクロ。
コレクション生成のために使われる。
一般に使われるfor文とは意味が違うことに注意する。

https://clojuredocs.org/clojure.core/for

構文が分かりづらいので、追加。

```clojure
(for [binding-form collection
      :when condition
      :let [binding-form value]]
  body)
```

`biding-form`: コレクションの各要素が格納される変数。
`collection`: 反復処理を行うコレクション。
`:when`: オプションの条件。
`:let`: オプションのローカル変数バインディング。
`body`: 各要素に対して適用する式。

```clojure
(for [x [1 2 3 4 5]]
  (+ x 1))
;; (2 3 4 5 6)

;; whenをつかって、フィルタリング
(for [x (range 10)
      :when (even? x)]
  x)
;; (0 2 4 6 8)

;; letをつかってローカル変数を定義
(for [x [1 2 3 4 5]
      :let [y (* x 2)]]
  y)
;; (2 4 6 8 10)

;; ネストしたループ
(for [x [1 2 3]
      y [10 20]]
  (* x y))
;; (10 20 20 40 30 60)
```

forは内容を掴みづらいので問題を書いておく。

- 1から10までの数字から偶数のみを取り出したリストを生成する。
- 2つのコレクション(`[1 2 3]`, `[4 5 6]`)のすべての組み合わせを作り、各ペアの和を要素とするリストの生成。
- 1から10までの数字で、その数字とその2乗のペアのリストを生成する。
- 1から5までの数をxとyに格納し、xがyより小さい場合にのみ`[x y]`のペアを要素とするリストを生成する。
- 1から5までの数をxとyとzに格納し、x + y + z = 6になる組み合わせを、`[x y z]`の形式で生成する。


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

## identity

`(identity x)`

> Returns its argument.

引用にあるように、引数xをそのまま返す関数。
そのままでは意味がないように思えるが、他の関数と組み合わせるために使用する。

https://clojuredocs.org/clojure.core/identity

```clojure
(identity 42)
;; 42

(identity "Hello, Clojure!")
;; "Hello, Clojure!"

;; someとの組み合わせ
(some identity [nil false 3 nil])
;; 3

;; filterとの組み合わせ
(filter identity [1 nil 2 false 3])
;; (1 2 3)
```

## instance?

`(instance? c x)`

オブジェクトが特定のクラスや型のインスタンスであるかを判定する関数。

https://clojuredocs.org/clojure.core/instance_q

```clojure
(instance? String "Hello")
;; true

(instance? Integer 42)
;; false
(instance? Long 42)
;; true

(instance? Boolean true)
;; true

(instance? clojure.lang.PersistentVector [1 2 3])
;; true

;; 独自の型も調べることができる
(defrecord Person [name age])

(def john (->Person "John" 30))

(instance? Person john)
;; true
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

## iterate

`(iterate f x)`

初期値から初めて、指定された関数を何度も繰り返し適用し、無限に続く遅延シーケンスを生成する関数。

https://clojuredocs.org/clojure.core/iterate

```clojure
(take 5 (iterate inc 0))
;; (0 1 2 3 4)

(take 6 (iterate #(- %) 1))
;; (1 -1 1 -1 1 -1)
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

## letfn

`(letfn [fnspecs*] exprs*)`

ローカル関数を定義するために使用される。
`let`が変数、`letfn`が関数を定義。

https://clojuredocs.org/clojure.core/letfn

```clojure
(letfn [(factorial [n]
          (if (<= n 1)
            1
            (* n (factorial (dec n)))))]
  (factorial 5))
;; 120

(letfn [(to-uppercase [s] (.toUpperCase s))
        (reverse-string [s] (apply str (reverse s)))]
  {:uppercase (to-uppercase "clojure")
   :reversed (reverse-string "clojure")})
;; {:uppercase "CLOJURE", :reversed "erujolc"}

;; 関数が相互参照している場合に役に立つ
(letfn [(even? [n]
          (if (zero? n)
            true
            (odd? (dec n))))
        (odd? [n]
          (if (zero? n)
            false
            (even? (dec n))))]
  (even? 4))
;; true
```

## line-seq

`(line-seq rdr)`

`java.io.BufferedReader`から行ごとの遅延シーケンスを生成するための関数。
遅延シーケンスを返す。
なお、リーダーオブジェクトが消費されるため、同じリーダーでの読み取りはできない。

https://clojuredocs.org/clojure.core/line-seq

```clojure
(require '[clojure.java.io :as io])

;; ファイルからの読み込み時に使用する
(with-open [rdr (io/reader "example.txt")]
  (doseq [line (line-seq rdr)]
    (println line)))

;; doallを使って、強制的にリストにすることも可能
(with-open [rdr (io/reader "example.txt")]
  (doseq [lines (doall (line-seq rdr))]
    (println lines)))

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

## loop

`(loop [bindings*] exprs*)`

`recur`との組み合わせで、回数の末尾再帰最適化(TCO)を実現する。

https://clojuredocs.org/clojure.core/loop

```clojure
;; 1から10までカウントする関数例。
(loop [n 1]
  (if (> n 10)
    (println "End")
    (do
      (println n)
      (recur (inc n)))))

;; 1から10までの累積和を計算する関数例。

(loop [n 1 sum 0]
  (if (> n 10)
    sum
    (recur (inc n) (+ sum n))))
```

## macroexpand

`(macroexpand form)`

マクロの展開を確認するための関数。
マクロがどのように展開するかを確認できる。
なお、引数はリストとして渡す必要がある。
そうでない場合はカッコの中身が評価されてしまうため。

https://clojuredocs.org/clojure.core/macroexpand

```clojure
;; 独自マクロを定義
(defmacro my-when [condition & body]
  `(if ~condition
     (do ~@body)))

;; マクロを展開したコードがプリントされる
(macroexpand '(my-when true (println "Hello, World!")))
;; (if true (do (println "Hello, World!")))

(defmacro my-unless [condition & body]
  `(my-when (not ~condition)
            ~@body))

;; 複数段階あるマクロもすべて展開される
(macroexpand '(my-unless false (println "Hello world!")))
;; (if (clojure.core/not false) (do (println "Hello world!")))

;; マクロであるwhenを展開した例
(macroexpand
 '(when true (println "True")
        (println "Still true")))
;; (if true (do (println "True") (println "Still true")))
```

## macroexpand-1

`(macroexpand-1 form)`

マクロの展開を確認するための関数。
指定したマクロを1段階だけ展開する。
なお、引数はリストとして渡す必要がある。
そうでない場合はカッコの中身が評価されてしまうため。

https://clojuredocs.org/clojure.core/macroexpand-1

```clojure
(defmacro my-when [condition & body]
  `(if ~condition
     (do ~@body)))

(defmacro my-unless [condition & body]
  `(my-when (not ~condition)
            ~@body))

;; 1段階のみ展開される
(macroexpand-1 '(my-unless false (println "Hello, World!")))
;; (user.core/my-when (clojure.core/not false) (println "Hello, World!"))

;; もう一段階展開して確認する
(macroexpand-1 '(my-when (not false) (println "Hello, World!")))
;; (if (not false) (do (println "Hello, World!")))
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

## map-indexed

`(map-indexed f)`
`(map-indexed f coll)`

コレクション内の各要素にインデックスを付けて関数を適用する関数。
pythonのenumerateのようなもの。

https://clojuredocs.org/clojure.core/map-indexed

```clojure
(map-indexed (fn [idx val] [idx val]) ["a" "b" "c"])
;; ([0 "a"] [1 "b"] [2 "c"])

;; インデックスが偶数番目の要素を2倍にする
(map-indexed (fn [idx val]
               (if (even? idx)
                 (* 2 val)
                 val))
             [1 2 3 4 5])
;; (2 2 6 4 10)
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

## meta

`(meta obj)`
メタデータを取得するための関数。

https://clojuredocs.org/clojure.core/meta

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

## neg?

`(neg? num)`

引数が負の数かどうかを判定する。


https://clojuredocs.org/clojure.core/neg_q

```clojure
(neg? -5)
;; true

(neg? 3)
;; false

(neg? 0)
;; false
```

## not-any?

`(not-any? pred col)`

シーケンス内で特定の条件を満たす要素が1つもないかどうかを判定する。
すべての要素に対して条件がfalseである場合にtrueを返し、1つでもtrueになる要素があればfalseを返す。
シーケンス内に条件にあう要素が「一つもない」ことを判定する場合に使う。

https://clojuredocs.org/clojure.core/not-any_q

```clojure
;; すべて正なのでtrue
(not-any? neg? [1 2 3 4 5])
;; true

;; 負の数があるのでfalse
(not-any? neg? [1 2 -3 4 5])
;; false

;; 一つも偶数が無いことを判定
(not-any? even? [1 3 5 7])
;; true

;; 偶数が含まれていることを判定
(not-any? even? [1 3 4 5])
;; false
```

## not-every?

`(not-every? pred coll)`

リストやベクタなどのコレクション内のすべての要素が、指定した条件を満たしていないかどうかをチェックする。
1つでも条件を満たしていない要素がないかをチェックしたい用途として使う(偶数の集合に奇数が混じっていないか、など)。

https://clojuredocs.org/clojure.core/not-every_q

```clojure
;; すべての要素が偶数でない
(not-every? even? [2 4 5 6])
;; true

;; すべての要素が条件を満たしている場合
(not-every? even? [2 4 6 8])
;; false

(not-every? even? [])
;; false
```

## not=

`(not= x)`
`(not= x y)`
`(not= x y & more)`

値が等しくないかどうかを判定する。
C言語やJavaScriptやJavaやRustなどでいう、`!=`と同じ働きをする。

https://clojuredocs.org/clojure.core/not=

```clojure
(not= 1 2)
;; true

(not= 1 1)
;; false

(not= 1 2 3)
;; true

(not= 1 2 2)
;; true
```

## parition

`(partition n coll)`
`(partition n step coll)`
`(partition n step pad coll)`

シーケンスを指定したサイズのサブシーケンスに分割する関数。
一定のサイズに分割したい場合に使う。

https://clojuredocs.org/clojure.core/partition


```clojure
(partition 2 [1 2 3 4 5 6])
;; ((1 2) (3 4) (5 6))

;; stepを指定する場合
(partition 2 1 [1 2 3 4 5 6 7 8 9 10])
;; ((1 2) (2 3) (3 4) (4 5) (5 6) (6 7) (7 8) (8 9) (9 10))

;; パディングを指定する場合。不足している要素を埋めてくれる
(partition 3 3 [0] [1 2 3 4 5])
;; ((1 2 3) (4 5 0))
```

## partition-by

`(partition-by f)`
`(partition-by f coll)`

シーケンスを、関数の結果に基づいて分割する関する。
関数の結果が変わるたびに新しいコレクションに分割される。

https://clojuredocs.org/clojure.core/partition-by

```clojure
(partition-by identity [1 2 2 3 3 3 4 4 4 4])
;; ((1) (2 2) (3 3 3) (4 4 4 4))

;; 奇数・偶数で分割
(partition-by even? [1 1 2 2 3 4 4 5 5 5 6 7])
;; ((1 1) (2 2) (3) (4 4) (5 5 5) (6) (7))

(partition-by #(first %) ["apple" "apricot" "banana" "blueberry" "cherry"])
;; (("apple" "apricot") ("banana" "blueberry") ("cherry"))
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

## pst

`(pst)`
`(pst e-or-depth)`
`(pst e depth)`

エラースタックトレースを表示する関数。
`clojure.repl`の読み込みが必要。

https://clojuredocs.org/clojure.repl/pst

```clojure
;; import
(require '[clojure.repl :refer [pst]])

(try
  (/ 1 0)
  (catch Exception e
    (pst e)))
  ; 
; ArithmeticException Divide by zero
; 
; 	clojure.lang.Numbers.divide (Numbers.java:190)
; 
; 	clojure.lang.Numbers.divide (Numbers.java:3911)
; 
;; 以下略
```

## rand

`(rand)`
`(rand n)`

ランダムな少数を生成する関数。

https://clojuredocs.org/clojure.core/rand

```clojure
;; 毎回違う結果の少数を生成
;; 0.0 〜 1.0
(rand)
;; 0.27664731043774426 <= 一例

;; nを指定できる
;; 0.0 〜 n (例では10.0が最大)
(rand 10)
;; 2.7176732712185747 <= 一例

;; 整数のランダム数が必要ならintを使う(もしくはrand-int）。
(int (rand 10))
;; 8 <= 一例

;; a以上、b未満なら以下のようにする。
(def a 10)
(def b 20)
(+ a (rand (- b a)))
;; 13.421563483733273 <= 一例
```

## rand-int

`(rand-int n)`

指定した範囲内のランダムな整数を生成するための関数。
0以上n未満の整数が返ってくる。

https://clojuredocs.org/clojure.core/rand-int

```clojure
;; 0以上10未満
(rand-int 10)

;; 1以上なら調整がいる。
;; 1以上、10未満
(+ 1 (rand-int 10))
```

## range

`(range)`
`(range end)`
`(range start end)`
`(range start end step)`

指定した範囲のシーケンスを生成する関数（数値のみ）。
遅延評価されるシーケンスとなっている。

https://clojuredocs.org/clojure.core/range

```clojure
;; そのままでも呼べるが、無限シーケンスとなり、実際には評価されない。
(range)

(range 5)
;; (0 1 2 3 4)

;; 3から8の前まで
(range 3 8)
;; (3 4 5 6 7)

;; 1から10までの間を2ずつ増加する
(range 1 10 2)
;; (1 3 5 7 9)
```

## re-find

`(re-find m)`
`(re-find re s)`

正規表現パターンに一致する部分を検索する関数。
一致する部分がある場合は最初の部分を、見つからなければnilを返す。

https://clojuredocs.org/clojure.core/re-find

```clojure
(re-find #"foo" "foo bar baz")
;; "foo"

(re-find #"\d+" "The answer is 42")
;; 42

;; キャプチャグループにも対応している
(re-find #"(\d+)-(\d+)" "Phone Number: 123-456")
;; ["123-456" "123" "456"]
```

## recur

再帰呼び出しを効率的に行うための関数。
関数の末尾で使用することで、末尾再帰最適化をした再帰処理が行える。
なお、関数の末尾で使用しないと、エラーとなる。

https://clojuredocs.org/clojure.core/recur

```clojure
;; 1から10までカウントする関数例。
(loop [n 1]
  (if (> n 10)
    (println "End")
    (do
      (println n)
      (recur (inc n)))))

;; 階乗を求める関数例。
(defn factorial [n]
  (letfn [(fact-helper [n acc]
            (if (zero? n)
              acc
              (recur (dec n) (* acc n))))]
    (fact-helper n 1)))

(factorial 5)
;; 120

;; 末尾以外で使用する場合
(loop [n 1]
  (if (> n 10)
    (println "End")
    (do
      (println n)
      (+ 1 (recur (inc n))))))
;; Syntax error (UnsupportedOperationException) compiling recur at (src/main/core.clj:12:12).
; Can only recur from tail position
```

## reduce

`(reduce f coll)`
`(reduce f val coll)`

コレクションの要素を1つずつ処理し、累積家かを生成するための関数。

https://clojuredocs.org/clojure.core/reduce

```clojure
(reduce + [1 2 3 4 5])
;; 15

;; 初期値を指定する
(reduce + 10 [1 2 3 4 5])
;; 25

;; リストの最大値を求める
(reduce #(if (> %2 %1) %2 %1) [1 5 3 9 2])
;; 9

;; マップの合計を計算する
(reduce #(+ %1 (second %2)) 0 {:a 1 :b 2 :c 3})
;; (reduce (fn [acc [k v]] (+ acc v)) 0 {:a 1 :b 2 :c 3})
;; 6

;; 奇数の積を合算する
(reduce * (filter odd? [1 2 3 4 5]))
;; 15
```

## repeat

`(repeat x)`
`(repeat n x)`

指定された要素を繰り返し生成するための関数。

https://clojuredocs.org/clojure.core/repeat

```clojure
(repeat 3 "A")
;; ("A" "A" "A")

;; 数値の指定がない場合は無限シーケンスを生成する
(take 5 (repeat "A"))
;; ("A" "A" "A" "A" "A") 

;; リストやベクタなどのコレクションも生成可能
(repeat 3 [1 2 3])
;; ([1 2 3] [1 2 3] [1 2 3])
```

## repeatedly

`(repeatedly f)`
`(repeatedly n f)`

指定した関数を繰り返し呼び出して得られる結果を生成する関数。
遅延シーケンスを返す。
`repeat`とは対象が関数であるか、要素であるか、といった違いがある。

https://clojuredocs.org/clojure.core/repeatedly

```clojure
(take 10 (repeatedly #(range 1 3)))
;; ((1 2) (1 2) (1 2) (1 2) (1 2) (1 2) (1 2) (1 2) (1 2) (1 2))


(def random-seq (repeatedly #(rand-int 10)))
(take 5 random-seq)
;; (5 5 8 4 0)

(repeatedly 3 (fn [] "Hello"))
;; ("Hello" "Hello" "Hello")

;; ランダムな英文字の生成
(defn random-alphabet []
  (char (+ (rand-int 26) 65)))

(take 10 (repeatedly random-alphabet))
;; (\I \H \W \T \J \Z \U \K \T \O)
```

## require

`(require & args)`

別の名前空間の関数や変数を、現在の名前空間にロードする関数。
REPLとプロジェクトで使用する場合に記法が違うことに注意する。

https://clojuredocs.org/clojure.core/require

```clojure
;; プロジェクトの中で使用する場合
(ns a-project.core
  (:require clojure.string))
(clojure.string/join "," ["a" "b" "c"])
;; "a,b,c"

;; エイリアスの利用
(ns a-project.core
  (:require [clojure.string :as str]))
(str/join "," ["a" "b" "c"])
;; "a,b,c"

;; 特定の関数や変数のみのインポート
(ns a-project.core
  (:require [clojure.string :refer [join]]))
(join "," ["a" "b" "c"])
;; "a,b,c"

;; すべての関数や変数をインポート
(ns a-project.core
  (:require [clojure.string :refer :all]))
(join "," ["a" "b" "c"])
;; "a,b,c"

;; REPLでの動的な使用
(require '[clojure.string :as str])
(str/join "," ["a" "b" "c"])
;; "a,b,c"
```

:::message
**なぜREPLだとシングルクォートが必要か**

なぜREPLだとシングルクォートがいるのかというと、`require`関数に渡す際には評価しないように渡す必要があるから。
シングルクォートをつけないと、シンボルとして解釈されてしまうので、シングルクォートをつけて、データとして扱うように指示する必要がある。

```clojure
;; clojure.coreのシンボルはロードされた名前空間なので問題なく処理される。
+
;; #function[clojure.core/+]

;; ロードされていないシンボルは、名前空間にないので、NotFoundExceptionとなってしまう。
clojure.string
; Syntax error (ClassNotFoundException) compiling at (src/core.clj:1:8697).
```

上記例の様に、ロードされたシンボルかを評価してしまうため、それを避けるためにシングルクォートをつける。

なお、`ns`の場合は`ns`側で適切に扱われるため、シングルクォートは不要となっている。
:::

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

:::message
**nextとrestの違い**

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
:::

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

## some

`(some pred coll)`

コレクションの各要素に関数を適用し、最初に条件に一致した値を返す。
わかりにくいが、関数を適用して、「nilでもfalseでもない」最初の値を返す関数。
なお、評価結果そのものが返ってくるため、trueで返す関数はtrueが返るし、数値を返す関数は数値が返る。

https://clojuredocs.org/clojure.core/some

```clojure
(some even? [1 3 4 7 9])
;; true

(some #{3} [1 2 3 4])
;; 3

(some #(if (even? %) %) [1 3 5 6 9])
;; 6

;; nilでもfalseでもない要素を抽出
(some identity [nil false 3 nil])
;; 3
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

## sort-by

`(sort-by keyfn coll)`
`(sort-by keyfn comp coll)`

コレクションをソートするための関数。
各要素に対して、指定したキー関数を適用し、その結果に基づいたソートを行う。

https://clojuredocs.org/clojure.core/sort-by


```clojure
(sort-by abs [3 -1 -5 2])
;; (-1 2 3 -5)

(def people [{:name "Alice" :age 30}
             {:name "Bob" :age 25}
             {:name "Charlie" :age 35}])

(sort-by :age people)
;; ({:name "Bob", :age 25} {:name "Alice", :age 30} {:name "Charlie", :age 35})

(sort-by :age (comp - compare) people)
;; ({:name "Charlie", :age 35} {:name "Alice", :age 30} {:name "Bob", :age 25})
```

## source

`(source n)`

関数やマクロのソースコードを表示するための関数。
通常、REPL上で関数やマクロを調べるのに使用する。

https://clojuredocs.org/clojure.repl/source

```clojure
;; import
(require '[clojure.repl :refer :all])

(source reduce)

(source filter)

(source cons)
```

## spit

`(spit f content & options)`

ファイルにデータを書き込む関数。

https://clojuredocs.org/clojure.core/spit

```clojure
(spit "example.txt" "Hello, Clojure!")

;; 追記する場合は :append をtrueにする
(spit "example.txt" "\nThis is a new line" :append true)

;; エンコーディングの指定
(spit "example-utf16.txt" "こんにちは" :encoding "UTF-16")

;; 文字列でないデータを渡すと文字列に変換する
(spit "example.txt" {:name "Clojure" :age 17})
;; 保存イメージ
;; {:name "Clojure", :age 17}
```

## split

`(split s re)`
`(split s re limit)`

文字列を指定した区切り文字（正規表現）で分割し、部分文字列のシーケンスを返す関数。

https://clojuredocs.org/clojure.string/split

```clojure
;; import
(require '[clojure.string :as str])

(str/split "hello world clojure" #" ")
;; ["hello" "world" "clojure"]

;; 分割の回数上限を指定できる
(str/split "one, two, three, four" #"," 2)
;; ["one" " two, three, four"]
```

## subs

`(subs s start)`
`(subs s start end)`

文字列の一部を切り出す関数。
開始位置や終了位置を指定できる。

https://clojuredocs.org/clojure.core/subs

```clojure
(subs "Clojure" 2)
;; "ojure"

;; 2からはじまり、5の手前まで(インデックス2-4)
(subs "Clojure" 2 5)
;; "oju"
```

## take

`(take n)`
`(take n coll)`

シーケンスの先頭から指定された数の要素を取得する関数。

https://clojuredocs.org/clojure.core/take

```clojure
(take 3 [1 2 3 4 5])
;; (1 2 3)

;; シーケンスが短い場合
(take 10 [1 2 3])
;; (1 2 3)

;; 無限シーケンスにも使用できる
(take 5 (range))
;; (0 1 2 3 4)

;; 他の関数と組み合わせることで、複雑なことが実現可能
(take 3 (drop 2 [1 2 3 4 5 6]))
;; (3 4 5)
```

## take-while

`(take-while pred)`
`(take-while pred coll)`

シーケンスの要素を条件にもとづいて取得するための関数。
指定された条件がtrueである限り取得し続ける。

https://clojuredocs.org/clojure.core/take-while

```clojure
(take-while #(< % 5) [1 2 3 4 5 6 7])
;; (1 2 3 4)

(take-while odd? [1 3 5 6 7 9])
;; (1 3 5)

;; 無限シーケンスでも使える
(take-while #(< % 10) (iterate inc 1))
;; (1 2 3 4 5 6 7 8 9)
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

## type

`(type x)`

引数として与えたオブジェクトの型情報を取得するための関数。

https://clojuredocs.org/clojure.core/type

```clojure
(type 42)
;; java.lang.Long

(type "hello")
;; java.lang.String

(type true)
;; java.lang.Boolean

(type [1 2 3])
;; clojure.lang.PersistentVector

(type '(1 2 3))
;; clojure.lang.PersistentList

(type {:a 1 :b 2})
;; clojure.lang.PersistentArrayMap

;; 独自の型も調べることができる
(defrecord Person [name age])

(def john (->Person "John" 30))

(type john)
;; user.core.Person
```

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

## when

`(when test & body)`

条件が真である場合のみ式を評価するマクロ
複数の式を実行できる。
なお、条件が偽の場合には何もしない。

https://clojuredocs.org/clojure.core/when

構文
```clojure
(when condition
  expr1
  expr2
  ...)
```

```clojure
(defn greet [name]
  (when (not (empty? name))
    (println "Hello, " name)
    (str "Greetings, " name "!")))

(greet "Alice")
;; Hello,  Alice
;; "Greetings, Alice!"
```

## with-open

`(with-open bindings & body)`

ファイル等のリソースを開いたときに、確実に閉じるために使用するマクロ。
pythonで言うところのwith文のようなもの。

https://clojuredocs.org/clojure.core/with-open

```clojure
(require '[clojure.java.io :as io])

;; ファイルからの読み込み時
(with-open [rdr (io/reader "example.txt")]
  (doseq [line (line-seq rdr)]
    (println line)))

;; ファイルへの書き込み時
(with-open [wtr (io/writer "example.txt")]
  (.write wtr "Hello, world!\n"))
```

## with-out-str

`(with-out-str & body)`

出力を文字列としてキャプチャするためのマクロ。
標準出力に出力されるデータを文字列として取り込めるので、`print`や`println`の結果を再利用できる。
また、標準出力を用いるテストなどにも使用できる。

https://clojuredocs.org/clojure.core/with-out-str

```clojure
(with-out-str
  (println "Hello, Clojure!")
  (println "This is test sentence."))
;; "Hello, Clojure!\nThis is test sentence.\n"
```

## zero?

`(zero? num)`

数値がゼロであるかどうかを判定する関数。

https://clojuredocs.org/clojure.core/zero_q

```clojure
(zero? 0)
;; true
(zero? 1)
;; false
(zero? -1)
;; false

(zero? 0.0)
;; true
(zero? 0N)
;; true
(zero? 0.0M)
;; true

```

## interpose

`(interpose sep)`
`(interpose sep coll)`

シーケンスの要素の間に指定した値を挿入する関数。
なお、新しいシーケンスとして返す。

https://clojuredocs.org/clojure.core/interpose

```clojure
(interpose 0 [1 2 3])
;; (1 0 2 0 3)

(interpose ", " ["a" "b" "c"])
;; ("a" ", " "b" ", " "c")

;; 文字列の結合などに利用する
(apply str (interpose ", " ["a" "b" "c"]))
;; "a, b, c"
```

## mapcat

`(mapcat f)`
`(mapcat f & colls)`

各要素に関数を適用して得られたシーケンスを結合する関数。
`map`と`concat`の組み合わせと思えばいい。

https://clojuredocs.org/clojure.core/mapcat

```clojure
(mapcat #(list % %) [1 2 3])
;; (1 1 2 2 3 3)

;; 以下のような形で動作している。
;; 最初がmap、次ですべてをconcatしているような感じ
;; 1 -> (1 1)
;; 2 -> (2 2)
;; 3 -> (3 3)
;; (1 1) (2 2) (3 3) => (1 1 2 2 3 3 )

(mapcat identity [[1 2] [3 4] [5 6]])
;; (1 2 3 4 5 6)

;; 実例としてCSVからのデータを整形する例
(def rows [["name" "age"] ["Alice" "17"] ["Bob" "25"]])
(mapcat #(concat % ["\n"]) rows)
;; ("name" "age" "\n" "Alice" "17" "\n" "Bob" "25" "\n")

;; (参考)mapの場合
(map #(list % %) [1 2 3])
;; ((1 1) (2 2) (3 3))
```
