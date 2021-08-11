import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement

/**
 * IRPatternHex.js を読み込む際は、<body>より後に書かないとだめみたいです（DOM生成後？）
 * */
fun main() {

    val button = document.getElementById("button")!!
    val patternBox = document.getElementById("pattern_box") as HTMLTextAreaElement
    val hexBox = document.getElementById("hex_box") as HTMLInputElement

    /** NECフォーマットの変調単位Tは562 */
    val NEC_T = 562

    // ボタン押したとき
    button.addEventListener("click", { event ->
        val patternText = patternBox.value
        val hexText = hexBox.value

        try {
            when {
                patternText.isNotEmpty() -> {
                    // パターン -> 16進数
                    val patternList = patternText
                        .replace(" ", "")
                        .split(",")
                        .map { text -> text.toInt() }
                    val bin = patternToBinCode(patternList)
                    val hex = binStringToHexString(bin)
                    hexBox.value = hex
                }
                hexText.isNotEmpty() -> {
                    // 16進数 -> パターン
                    val bin = hexStringToBinString(hexText)
                    val pattern = binCodeToPattern(NEC_T, bin)
                    patternBox.value = pattern.joinToString(separator = ", ")
                }
            }
        } catch (e: Exception) {
            window.alert("変換に失敗しました")
        }
    })
}

/**
 * [8955, 4510]を[Pair(8955, 4510)]にしていく関数。戻す際はflatMapを使ってください。
 * */
fun patternToOnOffPairList(patternList: List<Int>) =
    patternList
        .toMutableList()
        .mapIndexed { index, _ ->
            if ((index + 1) % 2 != 0) {
                Pair(patternList[index], patternList.getOrNull(index + 1) ?: 0)
            } else null
        }
        .filterNotNull()

/**
 * [patternList]のデータ部を2進数にして返す。
 *
 * ONとOFFが1:3の比率（だいたい）の場合は1、違う場合は0になる
 *
 * 32bit(32文字)になるはず
 *
 * リーダー部（9000,4500）（だいたいONが9000、OFFが4500）の次からがデータ部なので
 *
 * 例
 *
 * (607, 520) =（ON607、OFF520） なら 0
 * (607, 1703) =（ON607、OFF1703）なら 1
 *
 * 変換例
 * 010000 ...
 *
 * @param patternList ON/OFFパターン配列
 * */
fun patternToBinCode(patternList: List<Int>) =
    patternToOnOffPairList(patternList)
        .drop(1) // リーダー部を消す
        .dropLast(1) // ストップビット部も消す
        .map { (on, off) -> if (off > on * 2) "1" else "0" } // ONの2倍以上で T*3 ってことで
        .joinToString(separator = "") { it }

/**
 * ２進数からパターン生成。先頭にトレーラーつけて、最後にストップビットを入れる
 *
 * @param t 変調。NECなら 562 前後？
 * @param binCode ２進数
 * */
fun binCodeToPattern(t: Int, binCode: String) =
    listOf(t * 16, t * 9) + binCode.toList().flatMap { if (it == '1') listOf(t * 1, t * 3) else listOf(t * 1, t * 1) } + listOf(t * 1) // 1なら[T*1,T*3]、0なら[T*1,T*1]を配列に足していく

/**
 * 2進数の文字列を16進数の文字列に変換する
 *
 * "1010"を"A"に変換する
 *
 * @param binString 変換前2進数の文字列
 * */
fun binStringToHexString(binString: String) = "0x" + binString.toInt(2).toString(16)

/**
 * 16進数を2進数に戻す
 *
 * @param hexString 変換前16進数の文字列
 * */
fun hexStringToBinString(hexString: String) = hexString.replace("0x", "").toInt(16).toString(2)