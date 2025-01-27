---
title: "繰り返し構文の簡単なまとめ"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

# map

コレクションを作成する際に使う。
リストの各要素に対して関数を適用する場合、複数のリスト同士に関数を適用する場合に使用する。

```clojure
(map inc [1 2 3])
;; (2 3 4)

;; 少し回りくどいが、無名関数を使用することでも実現できる
(map (fn [x] (+ 1 x)) [1 2 3])
;; (2 3 4)

;; 無名関数だと、関数が存在していない場合でもカスタムで動作を拡張できる
(map (fn [x] (+ 2 x)) [1 2 3])
;; (3 4 5)

;; 複数のシーケンス間で関数を適用することも可能
(map + [1 2 3] [3 4 5])
;; (4 6 8)

;; 2つ以上のリストを使用できる
(map (fn [x y] [x y]) [1 2 3] [3 4 5])
;; ([1 3] [2 4] [3 5])
(map (fn [x y z] [x y z]) [1 2 3] [3 4 5] [7 8 9])
;; ([1 3 7] [2 4 8] [3 5 9])
```


# for

forはコレクションを変換する場合に使う。
mapで代替できることも多いが、より柔軟に、カスタマイズしたシーケンスを生成する場合に使用できる。

```clojure
(for [x (range 10)]
  x)
;; (0 1 2 3 4 5 6 7 8 9)

(for [x (range 10)
      :when (even? x)]
  x)
;; (0 2 4 6 8)

(for [x (range 10)
      :let [y (* x 2)]]
  y)
;; (0 2 4 6 8 10 12 14 16 18)

(for [x [1 2 3]
      y [10 20]]
  (* x y))
;; (10 20 20 40 30 60)

;; 文字列でも可能
(for [a ["a" "b" "c"]]
  a)
;; ("a" "b" "c")

(for [a ["a" "b" "c"]
      b ["Alice" "Bob" "Charie"]]
  (str a ":" b))

(for [a ["Alice" "Bob" "Charie"]
      :when (str/starts-with? a "A")]
  a)
;; ("Alice")

(for [a ["Alice" "Bob" "Charie"]
      :when (str/ends-with? a "e")]
  a)
;; ("Alice" "Charie")
```

## forとmapの違い

「同じく繰り返しをおこなってリストを返すのだから同じものでは？」と思ったけど、`for`の方が柔軟性があるし、同じことを再現しようとしたら可読性が高い。

以下はその例。
`for`でできることと引き較べて記載する。

複数のバインディングを使用する場合は、`for`の方が書きやすいし、読みやすい。

```clojure
;; forの場合
(for [x (range 3)
      y (range 3)]
  [x y])
;; ([0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2])

;; mapの場合
(apply concat (map (fn [x] (map (fn [y]
                                  [x y])
                                (range 3))) (range 3)))
;; ([0 0] [0 1] [0 2] [1 0] [1 1] [1 2] [2 0] [2 1] [2 2])
```

フィルタリングについても、`for`の方が可読性が高いように思える。

```clojure
;; forの場合
(for [x (range 10)
      :when (even? x)]
  x)
;; (0 2 4 6 8)

;; mapの場合
;; このままだとnilが残るので、削除が必要
(map (fn [x] (when (even? x) x)) (range 10))
;; (0 nil 2 nil 4 nil 6 nil 8 nil)

;; nilを排除したもの
;; remove版
(remove nil? (map (fn [x] (when (even? x) x)) (range 10)))
;; (0 2 4 6 8)

;; filter版
(filter some? (map (fn [x] (when (even? x) x)) (range 10)))
;; (0 2 4 6 8)
```

複数のフィルタリングを行う場合は、明らかに`for`の方が可読性が高い。

```clojure
(for [x (range 10)
      :when (odd? x)
      :when (>= x 3)]
  x)
;; (3 5 7 9)

(filter #(and (odd? %) (>= % 3)) (range 10))
;; (3 5 7 9)
```

バインディングによる書き方は、`map`の方が読みやすいという人もいるかも知れない。


```clojure
(for [x (range 5)
      :let [y (* x x)]]
  y)
;; (0 1 4 9 16)

(map (fn [x] (* x x)) (range 5))
;; (0 1 4 9 16)
```

しかし、さらに複雑にすると、やはり`for`の方が読みやすい。

```clojure
;; もうちょっと複雑な例
(for [x (range 1 11)
      :let [double (* 2 x)
            square (* double double)]
      :when (>= square 50)]
  square)
;; (64 100 144 196 256 324 400)

(filter some?
        (map
         (fn [x] (let [double (* 2 x)
                       square (* double double)]
                   (when (>= square 50)
                     square)))
         (range 1 11)))
;; (64 100 144 196 256 324 400)
```



