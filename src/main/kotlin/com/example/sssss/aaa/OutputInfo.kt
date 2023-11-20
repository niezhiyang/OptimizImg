package com.example.sssss.aaa

import java.io.File

class OutputInfo {

    var size = 0

    var type: String? = null

    var width = 0

    var height = 0

    var ratio = 0.0

    var url: String? = null

    @Transient
    var file: File? = null

    @Transient
    var filePng: File? = null

    override fun toString(): String {
        return "OutputBean{" +
            "size=" + size +
            ", type='" + type + '\'' +
            ", width=" + width +
            ", height=" + height +
            ", ratio=" + ratio +
            ", url='" + url + '\'' +
            '}'
    }
}