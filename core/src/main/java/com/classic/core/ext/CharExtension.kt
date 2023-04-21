@file:Suppress("unused")

package com.classic.core.ext

/**
 * Char extensions
 *
 * @author Classic
 * @date 5/14/21 15:48 PM
 */

/**
 * 是否是中文
 *
 * > **包含中文的：双引号、句号、逗号**
 *
 * - GENERAL_PUNCTUATION 判断中文的“号
 * - CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
 * - HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
 */
@Suppress("SpellCheckingInspection")
fun Char?.isChinese(): Boolean {
    if (null == this) return false
    val ub = Character.UnicodeBlock.of(this)
    return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
        ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
        ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
        ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
        ub == Character.UnicodeBlock.GENERAL_PUNCTUATION ||
        ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
        ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
}

/**
 * 是否是中文汉字
 *
 * > **不包含中文的：双引号、句号、逗号**
 */
fun Char?.isChineseText(): Boolean {
    if (null == this) return false
    val ub = Character.UnicodeBlock.of(this)
    return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
        ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
        ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
        ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
}

/**
 * 是否是大小写字母
 */
fun Char?.isCharLetter(): Boolean {
    if (null == this) return false
    return (this in 'a'..'z') || (this in 'A'..'Z')
}

/**
 * 是否是数字
 */
fun Char?.isNumber(): Boolean {
    if (null == this) return false
    return this in '0'..'9'
}

/**
 * 是否是Ascii码表的[33-126]的字符
 */
fun Char?.isAsciiChar(): Boolean {
    if (null == this) return false
    return this.code in 33..126
}