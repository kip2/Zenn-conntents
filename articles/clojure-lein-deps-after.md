---
title: "lein depsした後はCalvaを再起動しましょうって話"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: true
---

## 経緯

何度も同じ間違いをしては時間を無駄にしているので、備忘として記事にすることにした💢💢💢

---

## 症状

leiningenでプロジェクト管理していて、エディタはVSCode、拡張機能でCalvaを使用している。

まず、CalvaのREPLサーバが立ち上がっている状態で、`project.clj`にライブラリを追加する。

```clojure
:dependencies [[org.clojure/clojure "1.11.1"]
                [net.java.dev.jna/jna "5.10.0"]] ;; <= これを追加している
```

もちろんこのあと`lein deps`で、コマンドラインから依存関係の解決を行う。

```sh
lein deps
```

さて、この状態で`core.clj`等で読み込もうとすると、importに失敗する。

```clojure
(ns project1.core
  (:gen-class)
  (:import (com.sun.jna Native)))
;; ここのimportが失敗する。

;; 出力例
; Execution error (ClassNotFoundException) at java.net.URLClassLoader/findClass (URLClassLoader.java:445).
; com.sun.jna.Native
```

## 解決法

解決策は簡単。
Calvaを再起動しよう。

そうすれば無事にimportできる。

---

## あとがき

Calvaのことがまだ良くわかってないので、実はREPLサーバ立ち上がった状態でも読み込める方法があるのかもしれない。

そのあたりもいずれ調べて追記したい。

