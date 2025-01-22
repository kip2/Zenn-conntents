---
title: "Clojureのシーケンスに関する簡単なまとめ"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

# どういう記事？

毎度コード書く度、シーケンス周りの処理で「あれ？あれどうやるんだっけ...？」となることが増えてきたので、チートシートとしてまとめようと思った次第。

# シーケンスの種類

シーケンスとはなにか？

以下の書籍によると、シーケンスは`clojure.lang.ISeq`のインスタンスを指すようだ。

https://techbookfest.org/product/5160915401965568?productVariantID=6598377057812480

すべてのシーケンスではなく、主に使用するシーケンスである以下のものについて記載する。



