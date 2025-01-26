---
title: "繰り返し構文の簡単なまとめ"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

# map


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


```clojure


```