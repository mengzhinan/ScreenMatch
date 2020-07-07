package com.duke.screenmatch.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件流读写
 */
public class WriteFileLog {
    private FileOutputStream fileOutputStream;
    private File logFile;
    /**
     * 控制是否在上一次打开流的连续读写内容之后追加内容。<br/>
     * 与本次打开流后的连续读写无关。<br/>
     * 默认不保留以前的内容，只保留当前流的一系列写入操作。
     */
    private boolean isAppend = false;

    public WriteFileLog(File logFile) {
        this.logFile = logFile;
        init();
    }

    /**
     * 构造函数
     *
     * @param logFile
     * @param append
     */
    public WriteFileLog(File logFile, boolean append) {
        this.logFile = logFile;
        this.isAppend = append;
        init();
    }

    /**
     * 初始化文件流和日志文件
     */
    private void init() {
        checkFile(logFile);
        if (!createParentFolder(logFile)) {
            throw new IllegalArgumentException("create logFile.getParent() is failed");
        }
        createFile(logFile);
        try {
            fileOutputStream = new FileOutputStream(logFile, isAppend);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File logFile) {
        if (logFile == null || logFile.getParent() == null) {
            throw new IllegalArgumentException("logFile or logFile.getParent() is null");
        }
    }

    private boolean createParentFolder(File logFile) {
        checkFile(logFile);
        File parentFile = logFile.getParentFile();
        if (!parentFile.exists()) {
            if (!logFile.getParentFile().mkdirs()) {
                return false;
            }
        }
        return true;
    }

    private void createFile(File logFile) {
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭文件流
     */
    public void closeOutStreem() {
        if (fileOutputStream == null) {
            return;
        }
        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileOutputStream = null;
        }
    }

    /**
     * 写入内容
     *
     * @param bytes
     * @return
     */
    public boolean write(byte[] bytes) {
        if (fileOutputStream == null || bytes == null) {
            return false;
        }
        try {
            fileOutputStream.write(bytes, 0, bytes.length);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean write(String content) {
        if (content == null || content.length() == 0) {
            return false;
        }
        return write(content.getBytes());
    }

    /**
     * 换行
     *
     * @return
     */
    public boolean newLine() {
        return newLine(EnterChar.CRLF);
    }

    public boolean newLine(EnterChar enterChar) {
        if (enterChar == null) {
            return false;
        }
        return write(enterChar.value);
    }

    public enum EnterChar {
        CRLF("\r\n"), CR("\r"), LF("\n");

        private String value;

        private EnterChar(String value) {
            this.value = value;
        }
    }
}
