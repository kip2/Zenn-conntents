---
title: "rsut-analyzerがうまく働かないときの対処法"
emoji: "⚙️"
type: "tech" # tech: 技術記事 / idea: アイデア
topics: ["rust" ,"vscode"]
published: false
---

VScodeを使っていてrust-analyzerがうまく働かない事象にあたったので、解決した際のメモ。
割と汎用性あると思ったので、記事化しています。

## 事象

自分の環境で発生する事象は以下の2つ。

- ルートディレクトリ直下ではなく、深い階層の中で作ったCargoプロジェクトではrust-analyzerが効かない
- Rustのバージョンを固定してコードを書いているが、rust-analyzer側がそのバージョンのサポートを打ち切ったため、LSPが効かない

さて、一つ一つブレイクダウンして記載する。

## ルートディレクトリ直下でない場所にCargoプロジェクトを作ったときにrust-analyzerが効かない

VScodeでRustのプロジェクトを作成する際、通常はCargoプロジェクトを開くと思う。
そういった際にはrust-analyzerは通常通り働く。

しかし、以下のケースの場合、rust-analyzerがうまく働かない事象が発生する。

- ルートディレクトリにあるディレクトリの中で作ったCargoプロジェクト
- ルートディレクトリにあるディレクトリの中の中で作ったCargoプロジェクト
- 以下同様

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

補完が効いてる状態

![補完が効いてる状態](/images/pinning-rust-analyzer-version/pic1.png)

補完が効いてない状態

![補完が効いてない状態](/images/pinning-rust-analyzer-version/pic2.png)

こういう状態。

この状態でRustのコードを書くのは正直つらい。

### 解決策1

*VScodeで、プロジェクトのディレクトリをオープンする*

todo:

### 解決策2

*`.vscode/settings.json`にパスを記載する*


