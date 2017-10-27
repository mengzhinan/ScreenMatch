package com.duke.screenmatch.ui;

import com.duke.screenmatch.dp.Main;
import com.duke.screenmatch.listener.OnOkClickListener;
import com.duke.screenmatch.settings.Settings;
import com.duke.screenmatch.utils.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;

import javax.swing.*;
import java.awt.*;

public class MainAction extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        super.update(e);

    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        if (event == null) {
            return;
        }
        Project project = event.getData(PlatformDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        String not_show_dialog = Settings.get(Utils.getBasePath(project), Settings.KEY_NOT_SHOW_DIALOG);
        boolean notShowDialog = false;
        try {
            notShowDialog = Boolean.parseBoolean(not_show_dialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (notShowDialog) {
            String match_module = Settings.get(Utils.getBasePath(project), Settings.KEY_MATCH_MODULE);
            process(project, match_module);
        } else {
            SelectModuleDialog dialog = new SelectModuleDialog();
            dialog.setTitle("Select Module");
            dialog.setSize(Utils.getDialogWidth(), Utils.getDialogHeight());
            Point point = Utils.getDialogCenterLocation();
            dialog.setLocation(point.x, point.y);
            dialog.setModuleNames(project, dialog);
            dialog.getJList().setSelectedIndex(0);
            dialog.getJList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            dialog.setResizable(false);
            dialog.setOnOkClickListener(new OnOkClickListener() {
                @Override
                public void onOkClick(String selectString) {
                    Settings.setDefaultModuleName(selectString);
                    process(project, selectString);
                }
            });
            dialog.setVisible(true);
        }
        VirtualFileManager.getInstance().syncRefresh();
    }

    public void process(Project project, String moduleName) {
        if (project == null) {
            return;
        }
        if (Utils.isEmpty(moduleName)) {
            Messages.showMessageDialog("No modules are selected.", "Warning", Messages.getWarningIcon());
            return;
        }
        String basePath = Utils.getBasePath(project);
        String resBasePath = Utils.getResPath(basePath, moduleName);
        String tempBaseDP = Settings.get(basePath, Settings.KEY_BASE_DP);

        String[] needMatchs = null;
        String needMatchStr = Settings.get(basePath, Settings.KEY_MATCH_DP);
        if (!Utils.isEmpty(needMatchStr)) {
            needMatchs = needMatchStr.split(",");
        }

        String[] ignoreMatchs = null;
        String ignoreMatchsStr = Settings.get(basePath, Settings.KEY_IGNORE_DP);
        if (!Utils.isEmpty(ignoreMatchsStr)) {
            ignoreMatchs = ignoreMatchsStr.split(",");
        }

        /**
         * 字体(sp)是否也等比缩放
         */
        String match_font = Settings.get(basePath, Settings.KEY_IS_MATCH_FONT_SP);
        boolean matchFont = true;
        if (!Utils.isEmpty(match_font) && "false".equals(match_font.trim())) {
            matchFont = false;
        }

        boolean isUseNewFolder = true;
        try {
            String createValuesSWFolder = Settings.get(basePath, Settings.KEY_CREATE_VALUES_SW_FOLDER);
            isUseNewFolder = Boolean.parseBoolean(createValuesSWFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String resultMsg = Main.start(matchFont, tempBaseDP, needMatchs, ignoreMatchs, resBasePath, isUseNewFolder);
            Messages.showMessageDialog(resultMsg, "Tip", Messages.getInformationIcon());
        } catch (Exception e) {
            Messages.showMessageDialog("Failure, There may be some errors in your screenMatch.properties file.", "Error", Messages.getErrorIcon());
            e.printStackTrace();
        }
    }
}
