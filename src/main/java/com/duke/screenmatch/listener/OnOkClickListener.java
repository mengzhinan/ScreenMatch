package com.duke.screenmatch.listener;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public interface OnOkClickListener {
    void onOkClick(String selectString, List<VirtualFile> preferDimensFileList);
}
