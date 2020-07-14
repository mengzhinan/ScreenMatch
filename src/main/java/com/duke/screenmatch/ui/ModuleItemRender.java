package com.duke.screenmatch.ui;

import com.android.tools.idea.gradle.util.GradleUtil;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;

public class ModuleItemRender extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Module module = (Module) value;
        setIcon(GradleUtil.getModuleIcon(module));
        setText(module.getName());
        return this;
    }
}
