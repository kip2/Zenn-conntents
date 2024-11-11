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

## flatten

`(flatten x)`

ネストされたコレクションを1次元に変換する関数。

https://clojuredocs.org/clojure.core/flatten

```clojure
(flatten [1 [2 3] [4 [5 6]]])
;; (1 2 3 4 5 6)
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


