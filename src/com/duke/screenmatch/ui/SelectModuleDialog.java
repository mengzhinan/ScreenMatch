package com.duke.screenmatch.ui;

import com.duke.screenmatch.listener.OnOkClickListener;
import com.duke.screenmatch.utils.Utils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;

public class SelectModuleDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList list1;

    //提供一个回调
    private OnOkClickListener onOkClickListener;

    //对外暴漏列表对象
    public JList getJList() {
        return list1;
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
        DefaultListModel listModel = new DefaultListModel();
        JList jList = dialog.getJList();
        String[] nameArr = Utils.getModuleNames(Utils.getBasePath(project));
        if (nameArr == null || nameArr.length <= 0) {
            return;
        }
        for (String aNameArr : nameArr) {
            listModel.addElement(aNameArr);
        }
        jList.setModel(listModel);
    }

    public SelectModuleDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        // 处理点击事件
        if (this.onOkClickListener != null) {
            try {
                String moduleName = String.valueOf(getJList().getSelectedValue()).trim();
                onOkClickListener.onOkClick(moduleName);
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
