package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import com.duke.screenmatch.utils.Pair;
import com.google.common.base.Stopwatch;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 16:16
 * @UpdateTime: 2020-07-07 17:54
 * @Description: 入口
 */
public class Main {

    //默认支持的dp值
    private static final double[] DEFAULT_DPI_ARR = new double[]{
            384, 392,
            400, 410, 411, 432, 480,
            533, 592,
            600, 640, 662,
            720, 768,
            800, 811, 820,
            960, 961,
            1024, 1280, 1365};

    //生成的values目录格式(代码中替换XXX字符串)
    private static final String LETTER_REPLACE = "XXX";
    private static final String VALUES_OLD_FOLDER = "values-wXXXdp";//values-w410dp，这个目录需要删除
    private static final String VALUES_NEW_FOLDER = "values-swXXXdp";//values-sw410dp

    //是否删除旧的目录格式
    private static final boolean IS_DELETE_LEGACY_FOLDER = true;

    /**
     * 命令行入口
     *
     * @param args 命令行参数[注意，命令行是以空格分割的]
     */
    @Deprecated
    public static void main(String[] args) {
        System.out.println("Commandline process was disabled.");
    }


    /**
     * 适配文件调用入口
     *
     * @param params 设置参数
     * @return 返回消息
     */
    public static Pair<Boolean, String> start(Project project, SettingsParams params) {
        final HashSet<Double> dpiSet = new HashSet<>();

        if (params.isAddDefaultMatchDPIs()) {
            //添加默认的数据
            for (double aDefaultDPArr : DEFAULT_DPI_ARR) {
                dpiSet.add(aDefaultDPArr);
            }
        }
        if (params.getPreferMatchDPIs() != null) {
            for (double needMatch : params.getPreferMatchDPIs()) {
                if (needMatch > 0) {
                    dpiSet.add(needMatch);
                }
            }
        }
        if (params.getIgnoreMatchDPIs() != null) {
            for (double ignoreMatch : params.getIgnoreMatchDPIs()) {
                dpiSet.remove(ignoreMatch);
            }
        }

        System.out.println("基准宽度dp值：[ " + Tools.cutLastZero(params.getBaseDpi()) + " dp ]");
        System.out.println("本次待适配的宽度dp值: [ " + Tools.getOrderedString(dpiSet) + " ]");
        System.out.println("本次待适配的文件: " + Arrays.toString(params.getProcessFileArray()));

        String result = "Nothing to adapt";
        Stopwatch monitor = Stopwatch.createStarted();
        for (VirtualFile file : params.getProcessFileArray()) {
            result = matchSingleFile(project, params, dpiSet, file);
            if (result != null) {
                break;
            }
        }
        monitor.stop();
        System.out.println("Process screen match time cost : " + monitor.toString());
        return result == null
                ? Pair.create(true, "Over, adapt successful")
                : Pair.create(false, result);
    }

    /**
     * 无返回值表示没有出错
     */
    private static String matchSingleFile(Project project,
                                          SettingsParams params,
                                          HashSet<Double> dpiSet,
                                          VirtualFile baseDimensFile) {
        //获取基准的dimens.xml文件
        //判断基准文件是否存在
        String fileUrl = baseDimensFile.getPresentableUrl();
        if (!baseDimensFile.isValid()) {
            System.out.println("DK WARNING:  \\"+ fileUrl + "\" 路径下的文件找不到!");
            return "对应Module \"./res/values/" + baseDimensFile + "\" 路径下的文件找不到!";
        }
        //解析源dimens.xml文件
        ArrayList<XMLItem> list = XmlIO.readDimenFile(baseDimensFile);
        if (list == null || list.size() <= 0 || list.stream()
                .noneMatch(xmlItem -> xmlItem instanceof DimenItem)) {
            System.out.println("DK WARNING:  \\" + fileUrl + "\" 文件无数据!");
            return "\\" + fileUrl + "\" 文件未找到标签<dimen> or <item>!";
        } else {
            System.out.println("OK \\" + fileUrl + "\" 基准dimens文件解析成功!");
        }
        try {
            if (dpiSet.isEmpty()) {
                System.out.println("DK WARNING:  \"未找到匹配的match_dp配置, 请检查配置文件设置\"");
                return "未找到匹配的match_dp配置, 请检查配置文件设置";
            }
            //循环指定的dp参数，生成对应的dimens-swXXXdp.xml文件
            for (double dpi : dpiSet) {
                //获取当前dp除以baseDP后的倍数
                double multiple = dpi / params.getBaseDpi();

                //待输出的目录
                String outFolderName;
                //待删除的目录
                String delFolderName;
                //values目录上带的dp整数值
                String folderDP = String.valueOf((int) dpi);

                if (params.isCreateSmallestWithFolder()) {
                    outFolderName = VALUES_NEW_FOLDER.replace(LETTER_REPLACE, folderDP);
                    delFolderName = VALUES_OLD_FOLDER.replace(LETTER_REPLACE, folderDP);
                } else {
                    outFolderName = VALUES_OLD_FOLDER.replace(LETTER_REPLACE, folderDP);
                    delFolderName = VALUES_NEW_FOLDER.replace(LETTER_REPLACE, folderDP);
                }

                /*
                 * 生成新的目录values-swXXXdp
                 */
                //创建当前dp对应的dimens文件目录
                // .../res/values/dimens.xml
                // baseDimensFile
                // .../res/
                VirtualFile parent = baseDimensFile.getParent().getParent();
                // .../res/values-xwXXXdp/
                VirtualFile targetOutFolder = WriteAction.compute(() -> createChildDirectoryIfNotExist(project, parent, outFolderName));
                //生成的dimens文件的路径
                // .../res/values-xwXXXdp/dimens.xml
                VirtualFile targetOutFile = WriteAction.compute(() -> createChildFileIfNotExist(project, targetOutFolder, baseDimensFile.getName()));

                if (IS_DELETE_LEGACY_FOLDER) {
                    /*
                     * 删除以前适配方式的目录values-wXXXdp
                     */
                    VirtualFile delFolder = parent.findChild(delFolderName);
                    if (delFolder != null && delFolder.isValid()) {
                        WriteAction.run(() -> delFolder.delete(project));
                    }
                }

                //生成目标文件dimens.xml输出目录
                XmlIO.createDestinationDimens(project, params, new ArrayList<>(list), multiple, targetOutFile);
            }
            System.out.println("OK ALL OVER，全部生成完毕！");
            //适配完成
            return null;
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public static VirtualFile createChildDirectoryIfNotExist(Project project,
                                                             VirtualFile parent,
                                                             String name) throws IOException {
        final VirtualFile child = parent.findChild(name);
        return child == null ? parent.createChildDirectory(project, name) : child;
    }

    public static VirtualFile createChildFileIfNotExist(Project project,
                                                        VirtualFile parent,
                                                        String name) throws IOException {
        final VirtualFile child = parent.findChild(name);
        return child == null ? parent.createChildData(project, name) : child;
    }
}