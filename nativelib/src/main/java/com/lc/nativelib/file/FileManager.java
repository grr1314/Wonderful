package com.lc.nativelib.file;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 负责创建文件和文件夹
 */
public class FileManager implements IFileOperator {
    private static final String TAG = FileManager.class.getSimpleName();
    public static final int FLAG_SUCCESS = 1;//创建成功
    public static final int FLAG_EXISTS = 2;//已存在
    public static final int FLAG_FAILED = 3;//创建失败
    public static final String ANR_DIR_PATH = "/monitor/anr/";
    private final MMapFileOperator mMapFileOperator = new MMapFileOperator();
    private final JavaFileOperator javaFileOperator = new JavaFileOperator();
    private IFileOperator fileOperator = javaFileOperator;
    private Gson gson;
    public File dir;

    public FileManager(Gson gson) {
        this.gson = gson;
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * 创建 单个 文件
     *
     * @param filePath 待创建的文件路径
     * @return 结果码
     */
    public File createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Log.e(TAG, "The file [ " + filePath + " ] has already exists");
            return file;
        }
        if (filePath.endsWith(File.separator)) {// 以 路径分隔符 结束，说明是文件夹
            Log.e(TAG, "The file [ " + filePath + " ] can not be a directory");
            return null;
        }

        //判断父目录是否存在
        if (!file.getParentFile().exists()) {
            //父目录不存在 创建父目录
            Log.d(TAG, "creating parent directory...");
            if (!file.getParentFile().mkdirs()) {
                Log.e(TAG, "created parent directory failed.");
                return null;
            }
        }

        //创建目标文件
        try {
            if (file.createNewFile()) {//创建文件成功
                Log.i(TAG, "create file [ " + filePath + " ] success");
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "create file [ " + filePath + " ] failed");
            return null;
        }

        return null;
    }

    public boolean checkDir(String dirPath) {
        int state = createDir(dirPath);
        return state != FLAG_FAILED;
    }

    public int createDir(String dirPath) {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + dirPath;
        File dir = new File(path);
        //文件夹是否已经存在
        if (dir.exists()) {
            Log.w(TAG, "The directory [ " + dirPath + " ] has already exists");
            this.dir = dir;
            return FLAG_EXISTS;
        }
        if (!dirPath.endsWith(File.separator)) {//不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator;
        }
        //创建文件夹
        if (dir.mkdirs()) {
            Log.d(TAG, "create directory [ " + dirPath + " ] success");
            this.dir = dir;
            return FLAG_SUCCESS;
        }
        Log.w(TAG, "create directory [ " + dirPath + " ] fail");
        return FLAG_FAILED;
    }

    public boolean clear() {
        if (dir == null || dir.listFiles() == null) return false;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.delete()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public String readFromFile(@NonNull File file) {
        return fileOperator.readFromFile(file);
    }

    @Override
    public void writeToFile(@NonNull File file, @NonNull String content) {
        fileOperator.writeToFile(file, content);
    }
}
