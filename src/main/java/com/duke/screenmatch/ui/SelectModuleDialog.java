package com.duke.screenmatch.ui;

import com.android.builder.model.SourceProvider;
import com.android.tools.idea.gradle.project.model.AndroidModuleModel;
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
import java.util.stream.Collectors;

public class SelectModuleDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<Module> list_modules;
    private JCheckBox checkBox_show_all_files;
    private JList<HeaderItem> list_dimen_target;

    //提供一个回调
    private OnOkClickListener onOkClickListener;

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
        DefaultListModel<Module> listModel = new DefaultListModel<>();
        JList<Module> jList = dialog.getJList();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Arrays.sort(modules, Comparator.comparing(Module::getName));
        for (Module module : modules) {
            AndroidModuleModel model = AndroidModuleModel.get(module);
            if (model != null) {
                listModel.addElement(module);
            }
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

        list_dimen_target.setModel(new HeaderListModel());
        list_dimen_target.setCellRenderer(new HeaderItemRender());

        list_modules.setCellRenderer(new ModuleItemRender());
        list_modules.addListSelectionListener(e -> updateModuleDimenFiles());
        checkBox_show_all_files.addActionListener(e -> updateModuleDimenFiles());
    }

    private void updateModuleDimenFiles() {
        list_dimen_target.clearSelection();
        HeaderListModel model = (HeaderListModel) list_dimen_target.getModel();
        model.removeAllElements();

        boolean showAllFiles = checkBox_show_all_files.isSelected();

        Module module = list_modules.getSelectedValue();

        AndroidModuleModel androidModuleModel = AndroidModuleModel.get(module);
        if (androidModuleModel != null) {
            for (SourceProvider sourceProvider : androidModuleModel.getActiveSourceProviders()) {
                Header header = new Header(sourceProvider.getName());
                model.addHeader(header);

                boolean hideHeader = true;
                for (File resDirectory : sourceProvider.getResDirectories()) {
                    // list all files in values dir
                    File valuesDir = new File(resDirectory, "values");
                    VirtualFile valueDir = Utils.getVirtualFile(valuesDir.getPath());
                    if (valueDir != null && valueDir.isValid() && valueDir.isDirectory()) {
                        VirtualFile[] children = valueDir.getChildren();
                        for (VirtualFile file : children) {
                            String name = file.getName();
                            if (!showAllFiles && !name.contains("dimens")) {
                                continue;
                            }
                            model.addItem(new Item(file, name));

                            if (hideHeader) {
                                hideHeader = false;
                            }
                        }
                    }
                }
                // source provider has empty value file, hide the header
                if (hideHeader) {
                    model.removeHeader(header);
                }
            }
        }

        // select the default dimen file
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            HeaderItem element = model.getElementAt(i);
            if ("dimens.xml".equals(element.getText())) {
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
                String moduleName = parseValidModuleName(getJList().getSelectedValue()).trim();
                List<HeaderItem> selectedValuesList = list_dimen_target.getSelectedValuesList();
                onOkClickListener.onOkClick(moduleName, selectedValuesList.stream()
                        .filter(h -> h instanceof Item)
                        .map(h -> ((Item) h).component1())
                        .collect(Collectors.toList()));
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

    private static String parseValidModuleName(Module module) {
        String moduleName = module.getName();
        String projectName = module.getProject().getName().replace(" ", "_");
        return moduleName.startsWith(projectName + ".")
                        ? (moduleName.replace(projectName + ".", ""))
                        : moduleName;
    }

    public static void main(String[] args) {
        SelectModuleDialog dialog = new SelectModuleDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
