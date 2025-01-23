---
title: "Clojureのシーケンスに関する簡単なまとめ"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

# どういう記事？

コード書く度にシーケンス関係で「あれ？ アレってどうやって書くんだっけ...？」となることが増えてきたので、チートシートとしてまとめようと思った次第。

# シーケンスの種類

シーケンスとはなにか？

以下の書籍によると、シーケンスは`clojure.lang.ISeq`のインスタンスを指すようだ。

https://techbookfest.org/product/5160915401965568?productVariantID=6598377057812480

しかし、これらをすべて日常で使うわけではないので、代表的なものに絞りたい。
遅延シーケンスなどについては、(自分に)需要がでてきたらまとめることとしたい。

## この記事で扱うシーケンスについて

すべてのシーケンスではなく、主に使用するシーケンスである以下のものについて記載する。

- リスト
- ベクタ
- マップ
- セット

## 各シーケンスを作成する関数

各シーケンスを作成するには以下のような関数を使用して行う。

- リストは`list`関数
- ベクタは`vector`関数
- マップは`hash-map`関数
- セットは`hash-set`関数

```clojure
(list 1 2 3)
;; (1 2 3)

(vector 1 2 3)
;; [1 2 3]

(hash-map :a 1 :b 2 :c 3)
;; {:c 3, :b 2, :a 1}j

(hash-set 1 2 3)
;; #{1 3 2}
```

また、以上の出力結果を見ても分かる通り、マップとセットに関しては順序が保証されない。

文字列の場合はどうか？

```clojure
(list "a" "b" "c")
;; ("a" "b" "c")

(vector "a" "b" "c")
;; ["a" "b" "c"]

(hash-map :a "a" :c "c" :b "b")
;; {:c "c", :b "b", :a "a"}

(hash-set "a" "c" "b")
;; #{"a" "b" "c"}
```

ん？
マップとセットは、アルファベット順にソートされている？

## マップやセットを順序保証ありで作成する

調べるとどうやら違うらしい。
以下のようにソートしてから生成する専用の関数が存在しているため、あくまで偶然的な結果のようだ。

https://clojuredocs.org/clojure.core/sorted-map
https://clojuredocs.org/clojure.core/sorted-set

ソートした状態でマップやセットを作成する場合は、以下のようにする必要がある。

```clojure
(sorted-map :a "a" :c "c" :b "b")
;; {:a "a", :b "b", :c "c"}

(sorted-set "a" "c" "b")
;; #{"a" "b" "c"}
```

### マップやセットの順序をカスタムで指定して生成するやり方

なお、`sorted-map-by`や`sorted-set-by`を使用すれば、順序をカスタムした上でマップやセットが作成できる。

https://clojuredocs.org/clojure.core/sorted-map-by
https://clojuredocs.org/clojure.core/sorted-set-by

使い方は簡単で、与えたい要素の前に比較関数(comparator)を渡すだけだ。

`(sorted-map-by comparator & keyvals)`
`(sorted-set-by comparator & keys)`

```clojure
(sorted-set-by < 1 2 3 4)
;; #{1 2 3 4}

(sorted-set-by > 1 2 3 4)
;; #{4 3 2 1}
```

マップについては`<`や`>`をわたしてもソートできなかった。
どうやら`compare`でやらないとだめらしい。

```clojure
(sorted-map-by < :a 1 :b 2 :c 3)
;; => Execution error (ClassCastException)

;; compareなら比較が可能
;; キーに対してソートする模様
(sorted-map-by compare :a 7 :c 1 :b 3)
;; {:a 7, :b 3, :c 1}
```

急にでてきた`compare`とはなんぞや？

https://clojuredocs.org/clojure.core/compare

```clojure
(compare 1 2)
;; -1
(compare 2 2)
;; 0
(compare 3 2)
;; 1

(compare "a" "b")
;; -1
(compare "b" "b")
;; 0
(compare "c" "b")
;; 1
```

どうやら、2つの引数を比較して、
- 左の値が若ければ`-1`
- 同じ値であれば`0`
- 右の値が若ければ`1`
といった判定を行っている模様。

これを使い、その他のカスタム関数による比較を行うことができる。

`sorted-set-by`の場合。
```clojure
;; > をわたした場合と同じ
(sorted-set-by (fn [x y] (compare y x)) 3 1 2)

;; 文字列の長さによってソートする
(sorted-set-by (fn [x y] (compare (count x) (count y)))
               "apple" "banana" "diamond" "kiwi")
;; #{"kiwi" "apple" "banana" "diamond"}

;; 絶対値の大きさによる比較
(sorted-set-by (fn [x y] (compare (Math/abs x) (Math/abs y)))
               -10 5 -3 2)
;; #{2 -3 5 -10}

;; setなので、compareの結果が0になる要素は重複として排除される
(sorted-set-by (fn [x y] (compare (count x) (count y)))
               "apple" "banana" "cherry" "kiwi")
;; #{"kiwi" "apple" "banana"}

(sorted-set-by (fn [x y] (compare (Math/abs x) (Math/abs y)))
               -10 5 -5 10)
;; #{5 -10}
```

`sorted-map-by`の場合。
```clojure
;; キーの文字列の長さによってソート
(sorted-map-by (fn [k1 k2] (compare (count k1) (count k2)))
               "apple" 1 "banana" 2 "kiwi" 3)
;; {"kiwi" 3, "apple" 1, "banana" 2}

;; setと同様の用な感じになるが、なぜかcherryの値がbananaに統合されている
(sorted-map-by (fn [k1 k2] (compare (count k1) (count k2)))
               "apple" 1 "banana" 2 "cherry" 3)
;; {"apple" 1, "banana" 3}

;; キーを数値にすれば、絶対値による比較などが可能
(sorted-map-by (fn [k1 k2] (compare (Math/abs k1) (Math/abs k2)))
               -10 "a" 5 "b" -3 "c")
;; {-3 "c", 5 "b", -10 "a"}
```

## 各シーケンスの変換

シーケンスを別のシーケンスに変換したいことがあるので、その場合の書き方について。

`into`を使えば相互の変換が実現できる。

https://clojuredocs.org/clojure.core/into

```clojure
;; list -> vector
(into [] '(1 2 3))
;; [1 2 3]

;; vector -> list
;; リストはポップした順番で追加していってるので、逆順になる
(into '() [1 2 3])
;; (3 2 1)

;; list -> set
;; setは順序が保証されない
(into #{} '(1 2 3))
;; #{1 3 2}

;; vector -> set
(into #{} [1 2 3])
;; #{1 3 2}

;; set -> list
(into '() #{1 3 2})
;; (2 3 1)

;; set -> vector
(into [] #{1 3 2})
;; [1 3 2]
```
