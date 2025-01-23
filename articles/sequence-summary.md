---
title: "Clojureのシーケンスに関する簡単なまとめ"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: true
---

# どういう記事？

コード書く度にシーケンス関係で「アレってどうやって書くんだっけ...？」となることが増えてきたので、チートシートとしてまとめようと思い、作成したのがこの記事となります。

# シーケンスの種類

さて、そもそもシーケンスとはなんでしょうか？

以下の書籍によると、シーケンスは`clojure.lang.ISeq`のインスタンスを指すようです。

https://techbookfest.org/product/5160915401965568?productVariantID=6598377057812480

しかし、これらをすべて日常で使うわけではないので、今回の記事では代表的なものに絞りたいと思います。
遅延シーケンスなどについては、(自分に)需要がでてきたらまとめるかもしれません。

## この記事で扱うシーケンスについて

この記事では、以下のシーケンスが対象となります。

- リスト
- ベクタ
- マップ
- セット

---

さて、ここから各パターン事に記載していきます。
イメージは逆引き（Clojure初心者故にそこまでの精度はないですが）。

# 各シーケンスを作成する

各シーケンスを作成するには以下のような関数を使用して行います。
作成したいシーケンスの種類によって、それぞれ関数が用意されている感じです。

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

なお、以上の出力結果を見ても分かる通り、マップとセットに関しては順序が保証されません。

文字列の場合の順序保証はどうでしょうか？

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

簡単にAIに聞いた感じでは、アルファベット順のソートというわけではないようです。
以下のようにソートしてから生成する専用の関数が存在しているくらいなので、おそらく偶然的な結果と思われます。

https://clojuredocs.org/clojure.core/sorted-map
https://clojuredocs.org/clojure.core/sorted-set

ソートした状態でマップやセットを作成する場合は、以下のようにする必要があります。

```clojure
(sorted-map :a "a" :c "c" :b "b")
;; {:a "a", :b "b", :c "c"}

(sorted-set "a" "c" "b")
;; #{"a" "b" "c"}
```

### マップやセットの順序をカスタムで指定して生成するやり方

なお、`sorted-map-by`や`sorted-set-by`を使用すれば、順序をカスタムした上でマップやセットが作成できます。

https://clojuredocs.org/clojure.core/sorted-map-by
https://clojuredocs.org/clojure.core/sorted-set-by

使い方は簡単で、与えたい要素の前に比較関数(comparator)を渡すだけ。

`(sorted-map-by comparator & keyvals)`
`(sorted-set-by comparator & keys)`

```clojure
(sorted-set-by < 1 2 3 4)
;; #{1 2 3 4}

(sorted-set-by > 1 2 3 4)
;; #{4 3 2 1}
```

マップについては`<`や`>`をわたしてもソートできませんでした。
どうやら`compare`を使わないとできない模様。

```clojure
(sorted-map-by < :a 1 :b 2 :c 3)
;; => Execution error (ClassCastException)

;; compareなら比較が可能
;; キーに対してソートする模様
(sorted-map-by compare :a 7 :c 1 :b 3)
;; {:a 7, :b 3, :c 1}
```

さて、「急にでてきた`compare`とはなんぞや？」となったので、軽くどのような関数であるかを調査しました。

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

これを使い、その他のカスタム関数による比較を行うことができます。

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

# 各シーケンスの変換

シーケンスを別のシーケンスに変換したいことがあるので、その場合の書き方について。

`into`を使えば相互の変換が実現できます。

https://clojuredocs.org/clojure.core/into

`map`はちょっと特殊なので、`list`と`vector`と`set`について記載します。

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

# おわりに

Clojure初心者なので、一旦簡単な範囲でまとめを行いました。
実際にはシーケンスは多様な種類があるので、今後使用する機会がでてくればそのような関数も記事にしたいと思います。

特にClojureの特徴でもある遅延シーケンスについては、いつかバリバリさわっていきたいな...

