package com.gakk.noorlibrary.model.calender

/**
 * @AUTHOR: Mehedi Hasan
 * @DATE: 4/29/2021, Thu
 */
data class IslamicChhutiModel(
    var chutiTitle: String? = null,
    var gragrian: String? = null,
    var iDate: String? = null,
    var id: String? = null,
    val strArr: Array<String>
) {
    init {
        val length = strArr.size
        if (length != 1) {
            if (length != 2) {
                if (length != 3) {
                    if (length == 4) {
                        gragrian = strArr[3]
                    }
                }
                iDate = strArr[2]
            }
            chutiTitle = strArr[1]
        }
        id = strArr[0]
    }

}

