package com.duke.screenmatch.ui;

import com.duke.screenmatch.listener.OnOkClickListener;
import com.duke.screenmatch.utils.Utils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SelectModuleDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<Module> list_modules;
    private JList<String> list_dimen_target;

    //提供一个回调
    private OnOkClickListener onOkClickListener;

    private Project mProject;

    //对外暴漏列表对象
    public JList<Module> getJList() {
        return list_modules;
    }

    //提供设置监听函数
    public void setOnOkClickListener(OnOkClickListener onOkClickListener) {
        this.onOkClickListener = onOkClickListener;
    }

    //封装设置数据函数
    public void setModuleNames(Project project, SelectModuleDialog dialog) {
        if (dialog == null) {
            return;
        }
        mProject = project;
        DefaultListModel<Module> listModel = new DefaultListModel<>();
        JList<Module> jList = dialog.getJList();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Arrays.sort(modules, Comparator.comparing(Module::getName));
        for (Module module : modules) {
            listModel.addElement(module);
        }
        jList.setModel(listModel);
    }

    public SelectModuleDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        list_dimen_target.setModel(new DefaultListModel<>());

        list_modules.setCellRenderer(new ModuleItemRender());
        list_modules.addListSelectionListener(e -> updateModuleDimenFiles());
    }

    private void updateModuleDimenFiles() {
        Module module = list_modules.getSelectedValue();
        DefaultListModel<String> model = (DefaultListModel<String>) list_dimen_target.getModel();
        String basePath = Utils.getBasePath(mProject);
        String resBasePath = Utils.getResPath(basePath, module.getName());


        list_dimen_target.clearSelection();
        model.removeAllElements();

        // res/values
        // src/main/res/values todo: support flavor resource dir
        // list all files in values dir
        VirtualFile valueDir = Utils.getVirtualFile(resBasePath + File.separator + "values");
        if (valueDir != null && valueDir.isValid() && valueDir.isDirectory()) {
            VirtualFile[] children = valueDir.getChildren();
            for (VirtualFile file : children) {
                String name = file.getName();
                model.addElement(name);
            }
        }

        // select the default dimen file
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            String element = model.get(i);
            if ("dimens.xml".equals(element)) {
                list_dimen_target.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onOK() {
        // add your code here
        // 处理点击事件
        if (this.onOkClickListener != null) {
            try {
                String moduleName = String.valueOf(getJList().getSelectedValue()).trim();
                List<String> selectedValuesList = list_dimen_target.getSelectedValuesList();
                onOkClickListener.onOkClick(moduleName, selectedValuesList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        SelectModuleDialog dialog = new SelectModuleDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
