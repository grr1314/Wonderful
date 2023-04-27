package com.lc.nativelib.file;

import android.util.Log;

import androidx.annotation.NonNull;

import com.lc.nativelib.file.IFileOperator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JavaFileOperator implements IFileOperator {

    @NonNull
    @Override
    public String readFromFile(File targetFile) {
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();
        try {
            String line = "";
            bufferedReader = new BufferedReader(new FileReader(targetFile));
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    @Override
    public void writeToFile(File targetFile, String content) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(targetFile));
            bufferedWriter.write(content);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
