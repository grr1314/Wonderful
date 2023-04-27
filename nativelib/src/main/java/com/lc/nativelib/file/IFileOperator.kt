package com.lc.nativelib.file

import java.io.File

interface IFileOperator {
    fun readFromFile(file: File):String
    fun writeToFile(file: File, content: String)
}