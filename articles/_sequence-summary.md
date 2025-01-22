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

```

---
---
---

- [ ] mapやsetにわたす際のわたし方には他にバリエーションはないのか？
- [ ] 
