# Java ToDo アプリ

## 要件

Java を学習することを目的に、タスク管理が可能な Todoリスト を実現するアプリケーションです。  

## 仕様

本アプリケーションの仕様については [仕様書](https://github.com/teppei19980914/TodoList/blob/main/%E4%BB%95%E6%A7%98%E6%9B%B8.md) をご参照ください。  

## ひとこと

このプロジェクトは、Java学習の一環として作成しました。  
粗末なソースコードですが、ぜひ使ってみてください！  

## ✅ 主な機能

TodoListアプリケーションは、以下の機能を提供します。  
詳細は [設計書](https://github.com/teppei19980914/TodoList/blob/main/%E8%A8%AD%E8%A8%88%E6%9B%B8.md) をご参照ください。  

* タスクの追加、削除、更新
* タスクの完了状態の管理
* タスクの期限日を基準とした自動ソート
* CSVファイルを用いたタスクの保存・読み込み
* 外部CSVファイルからのタスク一括登録
* タスクの優先度と期限切れ状態の自動判定
* GUIを通じた直感的な操作

## 📁 ディレクトリ構成

```TEXT
.
├── TaskManager.java # メインの Java コード
├── Task.java # タスクをストアするための Java コード
├── TaskTableCellRenderer.java # 行の塗りつぶしをするための Java コード
└── README.md # このファイル
```

## 🚀 実行方法

### 1. Java のインストール

Java 8 以上の JDK がインストールされている必要があります。  
以下のコマンドでバージョンを確認できます。  

```bash
java -version
```

### 2. コンパイルと実行

同ディレクトリ上で下記コマンドを実行してください。  

```bash
javac .\*.java
java TaskManager
```

## ライセンス

このプロジェクトは [MIT License](https://opensource.org/license/MIT) のもとで公開されています。自由に使用、改変、再配布が可能です。  
