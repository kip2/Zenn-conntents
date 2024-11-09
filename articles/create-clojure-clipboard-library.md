---
title: "ClojureのREPLでクリップボード操作するためのライブラリを、作成して公開するまで"
emoji: "🐈️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["clojure"]
published: false
---

## 経緯

ClojureのREPLを触っているときに、マウスを使っていちいちコピペするのが面倒になったので、関数から気軽にクリップボードコピーできればいいな、と思って作成しようと思い立ったのが経緯。
macでいうところの`pbcopy`のような感じで、REPL上でコピーがしたかっのだ。

こういう感じのやつ。

```sh
$ cat file-name | grep pbcopy
```

実際にはREPL上で扱うので、シェルコマンドと全く同じとはいかないが、`(clipboard "want-to-copy-text")`の様な感じで、気軽にクリップボードにコピーがするライブラリを作成して、手順を記事化する試み。

あとあと手順絶対忘れるマンなので、詳細に手順を書いており、記載が冗長かも？

---

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
$ git clone <コピーしたアドレス>

# 例として、HTTPSだと以下の形式
# git clone https:githb.com/user-name/repository-name.git

# 例として、SSHだと以下の形式
# git clone git@github.com:user-name/repository-name.git
```

あとはクローンしたディレクトリをエディタで開く。

筆者はVSCodeを使用しているので、以下のコマンドでディレクトリを開く。

```sh
# クローンしたリポジトリのディレクトリに移動する。
$ cd <repository-name>
# カレントディレクトリをVSCodeで開く。
$ code .

# 以下の形式でも開ける。
$ code <repository-name>
```

このあとはVSCode上のターミナルから操作を行う。

## プロジェクトを作成

先ほど開いたディレクトリ内で、以下のコマンドを使用してプロジェクトを作成する。

```sh
$ lein new clj-clip
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

そして、動作確認用の`Hello, World!`を書く。

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

さて、あとはプロジェクトのルートディレクトリで以下のコマンドを打ち、動作を確認しよう。

```sh
$ lein run
# Hello Clojure!と表示されればOK
```

これで動作が確認できたので、先程書いたmain関数や`project.clj`は削除し、元の状態に戻しておこう。

:::

---

# コードの作成

## 今回のライブラリについて

今回作成するライブラリの要件を整理しておこう

- REPL上からクリップボードへのコピーをしたい。

主には上記の1点だが、一応以下の要件も入れておく。

- クリップボードから値を取得する。(REPL上で扱うかは微妙だが、シンメトリーとして作成する)

つまり、クリップボードへの入出力をするライブラリを作成するということだ。

## プロジェクトの説明文などを修正

さて、実際にコードを書く前にプロジェクトの設定を変更しておく。
`project.clj`の説明文とURLを変更する。

```clojure
(defproject clj-clip "0.1.0-SNAPSHOT"
;;   :description "FIXME: write description"
  :description "A simple library for handling the clipboard within the Clojure REPL."
;;   :url "http://example.com/FIXME"
  :url "https://github.com/kip2/clj-clip"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :repl-options {:init-ns clj-clip.core})
```

## テストコードを書く

では、実際にコードを書いていこう。
まずはテストコードから書く。

やりたいことはクリップボードの出し入れだけなので、クリップボードからの取得と、クリップボードへのコピーをテストする。

`test/clj-clip/core.clj`
```clojure
(ns clj-clip.core-test
  (:require [clojure.test :refer :all]
            [clj-clip.core :refer :all]))

(deftest clipboard-test
  (testing "クリップボードへのデータ保存と取得"
    (let [data "Hello, clipboard!"]
      (write-clip data)

      (is (= data (read-clip))))))
```

`write-clip`と`raed-clip`は、TDDの作法に則って存在するものとして呼び出す。

これで、テストを実行するとレッドバーになるので、最小限のコードを書いてグリーンバーにする。

`src/clj-clip/core.clj`
```clojure
(ns clj-clip.core
  (:require [clj-clip.core-test :refer :all]))

(defn write-clip [data]
  nil)

(defn read-clip []
  "Hello, clipboard!")
```

:::message
厳密なTDDにはなってないと思うけど、いまだお作法がわかっていない不調法ものなので、見逃してほしいです...
やめて...まさかりなげないで...
:::

## 実装コードの中身を書いていく

テストが通ったので、あとは実装していく。

クリップボードから取得するには何を使えばいいのだろうか?
調べたところ、java.awt.Toolkitを使うと良いらしい。

https://qiita.com/kurogelee/items/d6dacf64a0b0fe575bb6
https://docs.oracle.com/javase/jp/8/docs/api/java/awt/datatransfer/Clipboard.html

## java.awtをインポート

さて、ではまずjava.awtをインポートする

```clojure
(ns clj-clip.core
  (:import [java.awt Toolkit]
)
```

## クリップボードとのコネクションを取得

クリップボードへのコネクションはどのように張るのだろうか？

https://allabout.co.jp/gm/gc/80702/
この記事によると、以下のコード例のようにクリップボードの取得をするらしい。
2行目のが該当のコード。

```java
String str = "Test"; // 保存するテキスト
Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
StringSelection selection = new StringSelection(str);
clipboard.setContents(selection, null);
```

これをClojureでも同様にやる。

```clojure
(defn clipboard []
    (.getSystemClipboard (Toolkit/getDefaultToolkit)))
```

これでコネクションを取得できた。

https://docs.oracle.com/javase/jp/8/docs/api/java/awt/Toolkit.html#getSystemClipboard--

## クリップボードにコピーするコードを書く

では、先程の引用コードをもとにして、同様にクリップボードへのコピーコードを書こう。
再度引用するコードの3行目と4行目が該当の箇所。

```java
String str = "Test"; // 保存するテキスト
Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
StringSelection selection = new StringSelection(str);
clipboard.setContents(selection, null);
```

Clojureに直す。

```clojure
;; importにStringSelectionを追加
(ns clj-clip.core
  (:require [clj-clip.core-test :refer :all])
  (:import [java.awt Toolkit]
           [java.awt.datatransfer StringSelection])) ;; <= StringSelectionを追加>

;; 以下のコードを追加
(defn write-clip [text]
  (let [clipboard (clipboard)
        selection (StringSelection. text)]
    (.setContents clipboard selection nil)))
```

https://docs.oracle.com/javase/jp/8/docs/api/java/awt/datatransfer/StringSelection.html
https://docs.oracle.com/javase/jp/8/docs/api/java/awt/datatransfer/Clipboard.html#setContents-java.awt.datatransfer.Transferable-java.awt.datatransfer.ClipboardOwner-

## ここまでの動作を確認

さて、クリップボードへのコピーコードは正しく書けただろうか？

テストコードで確かめたいが、これをテストコードで確認するのは難しいと考える。
なぜなら、クリップボードにコピーしたものを取ってこないと、ちゃんとコピーされたかがわからないからだ。
つまり、後ほど作る予定のクリップボードからの取得コードが必要なので、そこまで書かないとテストができないことになってしまうからだ。

ClojureやJavaのテストに詳しい人ならこの問題を解決する方法があるのかもしれないが、今の自分にはそこまで望めないので、手動でテストする。

REPLから呼び出して、貼り付けて目視確認すれば良い。

というわけで確認コードを書いて、確かめていこう。

```clojure
(write-clip "Hello, Test!")

;; 手動でペーストした結果は以下になる。

;; Hello, Test!
```

テスト成功したので、取得できたことがわかった。
次にいこう。

## クリップボードの内容を取得するコードを書く

さて、クリップボードからの内容取得のコードを書いていこう。

再度、こちらの記事から引用する。
https://allabout.co.jp/gm/gc/80702/

```java
Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
Transferable object = clipboard.getContents(null);
String str = "";
try {
  String str = (String)object.getTransferData(DataFlavor.stringFlavor);
} catch(UnsupportedFlavorException e){
  e.printStackTrace();
} catch (IOException e) {
  e.printStackTrace();
}
```

とりあえずほぼ忠実に書いてみる。

```clojure
(defn read-clip []
  (let [clipboard (clipboard)
        contents (.getContents clipboard nil)]
    (try
      (when (.isDataFlavorSupported contents DataFlavor/stringFlavor)
        (.getTransferData contents DataFlavor/stringFlavor))
      (catch UnsupportedFlavorException e
        (.printStackTrace e))
      (catch java.io.IOException e
        (.printStackTrace e)))))
```

https://docs.oracle.com/javase/jp/8/docs/api/java/awt/datatransfer/Clipboard.html#getContents-java.lang.Object-
https://docs.oracle.com/javase/jp/8/docs/api/java/awt/datatransfer/Transferable.html#isDataFlavorSupported-java.awt.datatransfer.DataFlavor-


さて、テストコードを実行して動作を確かめてみよう。

テストコードは再掲。

```clojure
(deftest clipboard-test
  (testing "クリップボードへのデータ保存と取得"
    (let [data "Hello, clipboard!"]
      (write-clip data)

      (is (= data (read-clip))))))
```

ちゃんとテストも通っているので、クリップボードの内容を取得できている。

![テストコード成功](/images/create-clojure-clipboard-library/pic5.png)

## ちょっとリファクタリング

今回書いたコードはClojureらしいコードだろうか？

本来はコードレビューをしてもらって、フィードバックをしてもらうのだろうが、残念ながら筆者はぼっち気質のため、気軽に話せるClojureに詳しい人はいない。

というわけで、chatGPT大先生にきいてみよう。

さて、色々とやりとりしたので途中経過は省略し、結果としては以下のコードになった。

```clojure
;; 外部からは呼ばれないので、ローカル関数に変更
(defn- clipboard []
  (.getSystemClipboard (Toolkit/getDefaultToolkit)))

;; クリップボードコネクションのローカル関数バインドを削除、直接生成するようにした。
(defn write-clip [text]
  (let [selection (StringSelection. text)]
    (.setContents (clipboard) selection nil)))


(defn read-clip []
  (let [contents (.getContents (clipboard) nil)]
    (try
        ;; whenではなくifに変更し、明示的に失敗した場合を処理するようにした。
      (if (.isDataFlavorSupported contents DataFlavor/stringFlavor)
        (.getTransferData contents DataFlavor/stringFlavor)
        ;; 失敗した場合のメッセージを出力
        (println "String data not available in clipboard"))
      (catch UnsupportedFlavorException e
        ;; printStackTraceを使わずに、明示的にログ出力したほうがClojureらしいので、明示的にログ出力。
        (println "Unsupported flavor error" (.getMessage e)))
      (catch java.io.IOException e
        ;; printStackTraceを使わずに、明示的にログ出力したほうがClojureらしいので、明示的にログ出力。
        (println "I/O error" (.getMessage e))))))
```

一行一行変更する中で、テストコードを実行しながら行ったので、問題なくリファクタリングできた。

---

# ライブラリの使用

さて、つくったライブラリを他のプロジェクトから使ってみよう。

## ローカルリポジトリへの登録

さて、まずはローカルリポジトリへの登録が必要らしいので行っていく。

`lein install`をすると良いらしい。
このコマンドを行うと、ローカルのMavenリポジトリにライブラリがインストールされ、他のプロジェクトでも使えるようになるとのこと。

```sh
$ lein install
# Created /Users/clj-clip/target/clj-clip-0.1.0-SNAPSHOT.jar
# Wrote /Users/clj-clip/pom.xml
# Installed jar and pom into local repo.
```

ちなみに、ローカルのmavenリポジトリである`~/.m2/repository`に登録されるらしい。
確かめてみよう。

```sh
$ ls ~/.m2/repository | grep clj-clip
$ clj-clip
```

たしかに格納されている。

中身はどうなっているのだろうか？

```sh
$ tree ~/.m2/repository/clj-clip
/Users/.m2/repository/clj-clip
└── clj-clip
    ├── 0.1.0-SNAPSHOT
    │   ├── _remote.repositories
    │   ├── clj-clip-0.1.0-SNAPSHOT.jar
    │   ├── clj-clip-0.1.0-SNAPSHOT.pom
    │   └── maven-metadata-local.xml
    └── maven-metadata-local.xml

3 directories, 5 files
```

どうやら、`jar`ファイルとしてリポジトリに登録されているようだ。

## ローカルのリポジトリから読み込んで使用する

別のプロジェクトからローカルのリポジトリを読み込んで使用してみよう。

といっても、やることは普段と変わらない。
まず、`project.clj`に依存関係を追加する。

```clojure
:dependencies [[org.clojure/clojure "1.11.1"]
                [clj-clip "0.1.0-SNAPSHOT"]] ; <= これを追加
```

あとは、実際に使用する側で読み込んで使うだけだ。

```clojure
;; import
(ns markdown-utils.core
  (:require [clj-clip.core :as clip]))

;; write-clipを使用
(clip/write-clip "Hello from clj-clip!")

;; read-clipを使用
(println (clip/read-clip))
```

---

# ライブラリのデプロイ

せっかくなので、つくったライブラリをClojarsにデプロイしてみよう。

https://clojars.org/

https://qiita.com/totakke/items/e20405be6c2cf55ec9ac


## Clojarsにユーザー登録をする。

Clojarsにユーザー登録する。
k
https://clojars.org/

特に言うこともないので、これは省略する。

## デプロイ用トークンを生成

Clojarsのダッシュボードから、デプロイ用のトークンを生成する。

![デプロイトークン作成](/images/create-clojure-clipboard-library/pic6.png)

`Create Token`をクリックすると、画面遷移してトークンが生成される (スクショをミスったので、スクショなし)。

トークンは一度しか表示されないので、手元に大事に保存しておくこと。

## project.cljを更新

`project.clj`に以下の項目を追加する。

```clojure
  :deploy-repositories [["clojars" {:url "https://repo.clojars.org"}]]
```

## デプロイコマンドの実行

```sh
$ lein deploy clojars
# ユーザー名とパスワードを求められる。
# ユーザー名はClojarsに登録したユーザー名を入力する。
Username: your-username
# パスワードは、Clojarsで作成したデプロイトークンを入力する。
Password: your-deploy-token

# デプロイ作業が行われる。
Created /Users/clj-clip/target/clj-clip-0.1.0.jar
Wrote /Users/clj-clip/pom.xml
Need to sign 2 files
[1/2] Signing /Users/clj-clip/target/clj-clip-0.1.0.jar
[2/2] Signing /Users/clj-clip/pom.xml
Sending com/github/kip2/clj-clip/0.1.0/clj-clip-0.1.0.jar (9k)
    to https://repo.clojars.org/
Sending com/github/kip2/clj-clip/0.1.0/clj-clip-0.1.0.pom (2k)
    to https://repo.clojars.org/
Sending com/github/kip2/clj-clip/0.1.0/clj-clip-0.1.0.jar.asc (1k)
    to https://repo.clojars.org/
Sending com/github/kip2/clj-clip/0.1.0/clj-clip-0.1.0.pom.asc (1k)
    to https://repo.clojars.org/
Could not find metadata com.github.kip2:clj-clip/maven-metadata.xml in clojars (https://repo.clojars.org)
Sending com/github/kip2/clj-clip/maven-metadata.xml (1k)
    to https://repo.clojars.org/
```

デプロイが終わると、無事にClojarsにライブラリが登録される。
![ライブラリの公開ページ](/images/create-clojure-clipboard-library/pic7.png)

## 最後に

本当はローカルで利用できるところまで作る予定だったが、せっかくならとClojarsに登録するところまで冒険ができた。