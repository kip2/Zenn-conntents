---
title: "seqとintoでの型変換のパフォーマンスの違い"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

# 概要

Clojureでシーケンスの型変換を行うことがあるが、`into`を素朴につかっていたらパフォーマンスに影響が出る事象が発生したので、回避方法をまとめた記事です。

# intoによる型変換


シーケンスの型変換を行う場合、`into`関数を以下のように使用する。

```clojure
(into [] '(1 2 3))
;; [1 2 3]

(into '() [1 2 3])
;; (3 2 1)

(into #{} '(1 2 3))
;; #{1 3 2}

(into #{} [1 2 3])
;; #{1 3 2}

(into '() #{1 3 2})
;; (2 3 1)

(into [] #{1 3 2})
;; [1 3 2]
```

https://clojuredocs.org/clojure.core/into

## intoによるパフォーマンスの問題

しかし、`into`で大量のデータを扱う場合、パフォーマンスが問題となりうる。
新しいデータを作成したうえで返却しているので、扱うデータの量が増えると、どうしても発生する問題だ。

```clojure
(time (into '() (range 100000)))
;; intoの出力は省略
; "Elapsed time: 9.009333 msecs"
```

## seqで変換すると早い

この場合、`seq`を使うと素早く変換が可能。

```clojure
(time (seq (range 100000)))
; "Elapsed time: 0.076542 msecs"

(time (into '() (range 100000)))
; "Elapsed time: 9.009333 msecs"
```

9倍くらい差がある。

## なぜこんなに早いのか？

しかし、なぜこんなに早いのだろうか？

調査した感じ、どうやら元のシーケンスのオブジェクトをそのまま使っている感じだ。

```clojure
(def a-vec [1 2 3])

(def a-seq (seq a-vec))

(identical? (get a-vec 0) (first a-seq))
;; true

(System/identityHashCode (get a-vec 0))
;; 16113555

(System/identityHashCode (first a-seq))
;; 16113555
```

https://clojuredocs.org/clojure.core/identical_q

しかし、`into`についても同様に調べると、同じオブジェクトを刺さいているように思える。

```clojure
(def a-list '(1 2 3))

(def a-vec2 (into [] a-list))

(identical? (get a-vec2 0) (first a-list))
;; true

(System/identityHashCode (get a-vec2 0))
;; 161113555

(System/identityHashCode (first a-list))
;; 161113555
```

# ここから蛇足

さて、このとき`seq`で変換している型はなんだろうか？

```clojure
;; リストはそのまま
(type '(1 2 3))
;; clojure.lang.PersistentList
(type (seq '(1 2 3)))
;; clojure.lang.PersistentList

;; ベクタはちょっと違う
(type [1 2 3])
;; clojure.lang.PersistentVector
(type (seq [1 2 3]))
;; clojure.lang.PersistentVector$ChunkedSeq

;; setは型からしてかなり違っている。
(type #{1 2 3})
;; clojure.lang.PersistentHashSet
(type (seq #{1 2 3}))
;; clojure.lang.APersistentMap$KeySeq

;; マップはちょっと違う
(type {:a 1 :b 2})
;; clojure.lang.PersistentArrayMap
(type (seq {:a 1 :b 2}))
;; clojure.lang.PersistentArrayMap$Seq
```

`into`での変換では以下のようになる。

```clojure
;; リスト
(type '(1 2 3))
;; clojure.lang.PersistentList
(type (into '() [1 2 3]))
;; clojure.lang.PersistentList

;; ベクタ
(type [1 2 3])
;; clojure.lang.PersistentVector
(type (into [] '(1 2 3)))
;; clojure.lang.PersistentVector

;; セット
(type #{1 2 3})
;; clojure.lang.PersistentHashSet
(type (into #{} '(1 2 3)))
;; clojure.lang.PersistentHashSet

;; マップ
(type {:a 1 :b 2})
;; clojure.lang.PersistentArrayMap
(type (into {} '([:a 1] [:b 2] [:c 3])))
;; clojure.lang.PersistentArrayMap
```

- [ ] 型が違っていることで、何が違うのかを調査する
  - [ ] そもそもVectors$ChunkedSeqのダラーはなにか？
- [ ] setに対してはソートなどが使えるので有利？

## 遅延シーケンス

遅延シーケンスというわけではなさそう。

```clojure
(instance? clojure.lang.LazySeq (seq (range 100000)))
;; false

(instance? clojure.lang.LazySeq (into '() (range 100000)))
;; false
```

どうやら同じオブジェクトを参照しているらしい。

```clojure
(def vec1 [1 2 3])

(def seq1 (seq vec1))

(identical? (get vec1 0) (first seq1))
;; true
```



- [ ] タイプは別であるということを示すこと
  - [ ] タイプが別であるなら、それはその型のシーケンスの動作ができるはずなので、それが可能かを確かめる


