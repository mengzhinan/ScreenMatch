package com.duke.screenmatch.ui;

import com.android.tools.idea.gradle.util.GradleUtil;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ModuleItemRender extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        Module module = (Module) value;
        setIcon(GradleUtil.getModuleIcon(module));
        String projectName = module.getProject().getName();
        String moduleName = module.getName();
        boolean mainModule = Objects.equals(moduleName, projectName);
        if (!mainModule) {
            moduleName = moduleName.replace(projectName + ".", "");
        }
        setText(moduleName);
        return this;
    }
}
