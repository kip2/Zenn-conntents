---
title: "rust-analyzerがうまく働かないときの対処法"
emoji: "⚙️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["rust" ,"vscode"]
published: true
---

VScodeを使っていてrust-analyzerがうまく働かない事象に遭遇したので、解決した際のメモ。
割と汎用性あると思ったので、記事化しています。

## 事象

自分の環境で発生する事象は以下の2つ。

- ルートディレクトリ直下ではなく、深い階層の中で作ったCargoプロジェクトではrust-analyzerが効かない
- Rustのバージョンを固定してコードを書いているが、rust-analyzer側がそのバージョンのサポートを打ち切ったため、LSPが効かない

さて、一つ一つ記載していく。

## ルートディレクトリ直下でない場所にCargoプロジェクトを作ったときにrust-analyzerが効かない

VScodeでRustのプロジェクトを作成する際、通常はCargoプロジェクトを開くと思う。
そういった際にはrust-analyzerは通常通り働く。

しかし、以下のケースの場合、rust-analyzerがうまく働かない事象が発生する。

- ルートディレクトリにあるディレクトリの中で作ったCargoプロジェクト
- ルートディレクトリにあるディレクトリの中の中で作ったCargoプロジェクト
- 同様に更に深い階層でも

例として、以下のような構成が考えられる

```sh
.
├── Some-Directory
├── Rust
│   ├── project1
│   │   ├── Cargo.lock
│   │   ├── Cargo.toml
│   │   └── src
│   │      └── main.rs  # <- 例えばここのmain.rsを開いた場合に補完が効かない
│   └── project2
│       ├── Cargo.lock
│       ├── Cargo.toml
│       └── src
│          └── main.rs
```

補完が効かない、とはどういうことかというと、

補完が効いている状態

![補完が効いている状態](/images/pinning-rust-analyzer-version/pic1.png)

補完が効いてない状態

![補完が効いてない状態](/images/pinning-rust-analyzer-version/pic2.png)

こういう状態。

この状態でRustのコードを書くのは正直つらい。

### 解決策1

*VScodeで、プロジェクトのディレクトリをオープンする*

これは単純な方法で、階層が深くなることでrust-analyzerが働かないなら、階層を低くしちゃえばいいじゃない、という方法。

どうやるかというと、VScodeで該当のディレクトリを開き直せば良い。

やり方は何でも良いが、`code`コマンドを使った例を示す。

```sh
code Rust/project1
```

このコマンドによって、該当のディレクトリをVScodeで開くことができる。

画像はVScodeで開き直した状態。
補完が効いているのがわかる。

![VScodeを開き直した状態](/images/pinning-rust-analyzer-version/pic3.png)

### 解決策2

*`.vscode/settings.json`にパスを記載する*

解決策1でもよいが、いちいち開き直すのは面倒だし、何より複数のディレクトリを管理した状態で補完を効かせたい、というケースもあると思う。

そういう場合には、`.vscode/settings.json`に、該当のCargoプロジェクトの`Cargo.toml`のパスを記入すると良い。

まずファイルを作成する。

```sh
mkdir .vscode
touch .vscode/settings.json
```

それから、`settings.json`に以下の内容を記載する。

```json
{
  "rust-analyzer.linkedProjects": [
    "Rust/project1/Cargo.toml"
  ],
}
```

画像は実際に行った例。

![settings.jsonを記載した状態](/images/pinning-rust-analyzer-version/pic4.png)

なお、パスは何個でも記載が可能なので、管理するCargoプロジェクトが増えても安心。

```json
{
  "rust-analyzer.linkedProjects": [
    "Rust/project1/Cargo.toml",
    // プロジェクトが増えたら、パスを追加すれば良い。
    "Rust/project2/Cargo.toml"
  ],
}
```

## Rustのバージョンを固定してコードを書いているが、rust-analyzer側がそのバージョンのサポートを打ち切ったため、LSPが効かない

Rustを書いているとき、バージョンを固定してプログラミングすることがある。

そういった場合に、rust-analyzerが固定したバージョンのRustのサポートを打ち切ることがある。

例えば、以下の構成で固定している場合に、エラーが発生した(2025年5月現在)。

`rust-toolchain.toml`
```toml
[toolchain]
channel = "nightly-2024-01-01"
components = ["rustfmt", "rust-src"]
targets = ["x86_64-unknown-linux-gnu"]
profile = "default"
```

![rust-analyzerでサポートが打ち切られている例](/images/pinning-rust-analyzer-version/pic5.png)

Google翻訳噛ませたもの
`ワークスペース ~/project1/Cargo.toml は古いツールチェーンバージョン 1.77.0 を使用していますが、rust-analyzer は 1.78.0 以降のみをサポートしています。ツールチェーンに rust-analyzer rustup コンポーネントを使用するか、ツールチェーンをサポート対象バージョンにアップグレードすることを検討してください。`

要するにRustを最新版に上げてくれってことなんだけど、理由があってバージョンを固定しているのだから、それは難しい。

### 解決策

やることは2つ。

1. 固定したバージョンのRustをサポートしているrust-analyzerをダウンロードする。
2. `.vscode/settings.json`に、ダウンロードしたrust-analyzerのパスを記載する。

#### 固定したバージョンのRustをサポートしているrust-analyzerをダウンロードする。

公式のリポジトリからダウンロードしてこよう。

`公式リポジトリ`
https://github.com/rust-lang/rust-analyzer

公式リポジトリの`Releases`ページからバイナリファイルをダウンロードできる。
自分の環境にあったバイナリファイルをダウンロードしよう。

`Releasesページ`
https://github.com/rust-lang/rust-analyzer/releases

該当するバージョンの`Assets`欄から選択する。

![ダウンロードページ](/images/pinning-rust-analyzer-version/pic6.png)

これを、解凍して、適当なディレクトリに設置したらOK。

なお、参考までに。
自分ではここまでの流れをシェルスクリプトにして実行した。

```sh
#!/bin/bash

# 設置するディレクトリを作成
mkdir -p rust-analyzer/bin
cd rust-analyzer/bin

# 対象のrust-analyzerをダウンロード
# 環境にあわせて、ダウンロードする対象を変えること
curl -L -o rust-analyzer-1.77.gz https://github.com/rust-lang/rust-analyzer/releases/download/2024-03-11/rust-analyzer-aarch64-apple-darwin.gz

# 解凍
gunzip rust-analyzer-1.77.gz
# 実行権限を付与
chmod +x rust-analyzer-1.77
```

#### `.vscode/settings.json`に、ダウンロードしたrust-analyzerのパスを記載する。

先程解凍したrust-analyzerのパスを、`.vscode/settings.json`に記載する。

```json
{
  "rust-analyzer.server.path": "rust-analyzer/bin/rust-analyzer-1.77"
}
```

記載した段階で、rust-analyzerのLSP再起動が必要になる。

VScode本体を再起動するか、コマンドパレットで`rust-analyzer: Restart server`を選択して再起動する。

これで該当ディレクトリでのrust-analyzerのバージョンが固定できたので、補完が効くようになる。

## 終わりに

いままで同様の問題が発生しては放置してたのを、今回思い切って問題を解消し、記事にまとめてみました。
割と同様の事象は発生すると思うので、同じ問題で困っている人の助けになれば幸いです。

