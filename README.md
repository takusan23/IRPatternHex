# 赤外線 NECフォーマット - 16進数変換機

赤外線パターンを16進数に、その逆をすることが出来るWebページです。

https://nec-ir-pattern-hex.netlify.app/

![Imgur](https://imgur.com/cs6t3lv.png)

# なんかKotlinで書いてあるんだけど？

https://takusan.negitoro.dev/posts/ir_nec_format/

で書いた`Kotlin`コードがもったいないので、`Kotlin/JS`でブラウザから実行できるようにした。  
結果としては最高でした。

# 実行方法
- このリポジトリをクローンするなりZipを落とすなりしてローカルへ入れてください。
- IDEAで開きます
- 終わるまで待ちます
- 右下？にある`Terminal`を押して、`gradlew browserRun --continuous`を実行します
- ブラウザが立ち上がる。これで完了。KotlinでJSの代わり出来るとか神か？？？

# ビルド方法 (HTML書き出し)

- Terminalで`gradlew browserWebpack`を実行します。
- `build/distributions`に生成されています。
- Webサイトを公開したければ、`Netlify`とか`GitHub Pages`とか`Vercel`で公開するようにすれば完了。なはずなんだけど、
    - なんかうまく行かなかったので`GitHub Actions`でビルドまでして、生成したファイルたちを`Netlify`で公開するようにした。
    - あとなんか`gradlew の権限がなんとか`って言われて失敗するので、ローカル環境で以下のコマンドを打ち込んでコミットして、プッシュする必要があります。
        - `git update-index --chmod=+x gradlew`

以下例

```yml
# 参考にした。thx!：https://qiita.com/nwtgck/items/e9a355c2ccb03d8e8eb0

name: Netlify

on:
  push:
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v2
      
      # HTML書き出し（ビルド）
      - name: Making html
        run: ./gradlew browserWebpack 
      
      # Netlifyにデプロイする
      - name: Upload netlify
        run: npx netlify-cli deploy --dir=./build/distributions --prod
        env:
          NETLIFY_AUTH_TOKEN: ${{ secrets.NETLIFY_AUTH_TOKEN }}
          NETLIFY_SITE_ID: ${{ secrets.NETLIFY_SITE_ID }}
```

# メモ
初期状態（生成直後）の`index.html`は、`<script>`タグが`<head>`タグ内に入ってるのですが、  
このままではDOM操作出来ないので、`<body>`の下に書く必要があります？

```html

<!DOCTYPE html>
<html lang="ja">

<head>
    <meta charset="UTF-8">
    <title>タイトル</title>
</head>

<body>

</body> 

<!-- ここでKotlin/JSのファイルを読み込む -->
<script src=""></script>

</html>
```
