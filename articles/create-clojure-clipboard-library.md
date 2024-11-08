---
title: "REPLでクリップボード操作するためのライブラリを作成する"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

## 経緯

Clojureを触っているときに、マウスを使っていちいちコピペするのが面倒になったので、関数から気軽にクリップボードコピーできればいいな、と思って作成しようと思い立ったのが経緯です。

macでいうところの`pbcopy`のような感じで、REPL上でコピーがしたかったのです。

こういう感じのやつ。
```sh
cat file-name | grep pbcopy
```

## リポジトリの作成

まずGithub上でリポジトリを作成する。

Githubのマイページから`Repositories`を選択し、`New`ボタンをクリックする。

![リポジトリ作成方法1](/images/create-clojure-clipboard-library/pic1.png)

リポジトリの設定画面が表示されるので、各種設定を行う。
詳細は画像を参照。

![リポジトリ作成方法2](/images/create-clojure-clipboard-library/pic2.png)

リポジトリが作成される。

![リポジトリ作成方法3](/images/create-clojure-clipboard-library/pic3.png)

## ローカル環境にクローンする

作成したリポジトリを、ローカル環境にクローンする。

リポジトリ画面で、`<>Code`をクリックし、アドレスをコピーする。
ssh設定を行っているなら`SSH`を、そうでないなら`HTTPS`でいいと思う。

![リポジトリ作成方法4](/images/create-clojure-clipboard-library/pic4.png)

コピーしたアドレスを使用し、ローカル環境で以下のコマンドを入力する。

```sh
git clone <コピーしたアドレス>

# 例として、HTTPSだと以下の形式
# git clone https:githb.com/user-name/repository-name.git

# 例として、SSHだと以下の形式
# git clone git@github.com:user-name/repository-name.git
```

あとはクローンしたディレクトリをエディタで開く。

筆者はVSCodeを使用しているので、以下のコマンドでディレクトリを開く。

```sh
# クローンしたリポジトリのディレクトリに移動する。
cd <repository-name>
# カレントディレクトリをVSCodeで開く。
code .

# 以下の形式でも開ける。
code <repository-name>
```

このあとはVSCode上のターミナルから操作を行う。

## プロジェクトを作成

先ほど開いたディレクトリ内で、以下のコマンドを使用してプロジェクトを作成する。

```sh
lein new clj-clip
```

これで環境の準備がおわった。
あとは実際にコードを作成していく作業となる。

:::message
プロジェクト作成自体はこれで良いのだが、個人的に、作成したプロジェクトがちゃんと動くかを確認したい。
ここでプロジェクトが動くのを確認しておけば、後々エラーが起きても、プロジェクト作成まわりについてはエラー原因から排除できるので。

癖になってんだ、グリーンバーを確認するの。

というわけで、まずエントリーポイントを記述する。

`project.clj`
```clojure
(defproject clj-clip "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main clj-clip.core ;; <= ここを追加
  :repl-options {:init-ns clj-clip.core})
```

そして、動作確認用のハローワールドを書く。

`src/clj-clip/core.clj`

```clojure
*(ns clj-clip.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; ここを追加
(defn -main [& args]
  (println "Hello, Clojure!"))

```

さて、あとはプロジェクトのルートディレクトリで以下のコマンドをうち、動作を確認しよう。

```sh
lein run
# Hello Clojure!と表示されればOK
```

これで動作が確認できたので、先程書いたmain関数や`project.clj`は削除し、元の状態に戻しておこう。

:::

---

# コードの作成



