package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;

import java.io.File;
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

    //基准dp，比喻：360dp
    private static final double DEFAULT_DP = 360;
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
    public static void main(String[] args) {
        //获取当前目录的绝对路径
        String resFolderPath = new File("./res/").getAbsolutePath();
        String tempBaseDP = null;
        String[] needMatchs = null;
        String[] ignoreMatchs = null;
        if (args != null && args.length > 0) {
            /**
             * 调用Main函数，默认数组第一个为基准适配dp值
             */
            tempBaseDP = args[0];
            ignoreMatchs = new String[]{};
            if (args.length > 1) {
                needMatchs = Arrays.copyOfRange(args, 1, args.length);
            }
        }

        SettingsParams.Builder builder = new SettingsParams.Builder()
                .setFontMatch(true)
                .setResFolderPath(resFolderPath)
                .setCreateSmallestWithFolder(true);

        if (tempBaseDP != null) {
            builder.setBaseDpi(Double.parseDouble(tempBaseDP));
        }

        if (needMatchs != null) {
            double[] needMatchDPIs = Arrays.stream(needMatchs)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            builder.setPreferMatchDPIs(needMatchDPIs);
        }

        if (ignoreMatchs != null) {
            double[] ignoreMatchDPIs = Arrays.stream(ignoreMatchs)
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            builder.setIgnoreMatchDPIs(ignoreMatchDPIs);
        }
        start(builder.build());
    }


    /**
     * 适配文件调用入口
     *
     * @param params 设置参数
     * @return 返回消息
     */
    public static String start(SettingsParams params) {
        double baseDPI;
        if (params.getBaseDpi() > 0) {
            baseDPI = params.getBaseDpi();
        } else {
            baseDPI = DEFAULT_DP;
        }

        final HashSet<Double> dataSet = new HashSet<>();

        if (params.isAddDefaultMatchDPIs()) {
            //添加默认的数据
            for (double aDefaultDPArr : DEFAULT_DPI_ARR) {
                dataSet.add(aDefaultDPArr);
            }
        }
        if (params.getPreferMatchDPIs() != null) {
            for (double needMatch : params.getPreferMatchDPIs()) {
                if (needMatch > 0) {
                    dataSet.add(needMatch);
                }
            }
        }
        if (params.getIgnoreMatchDPIs() != null) {
            for (double ignoreMatch : params.getIgnoreMatchDPIs()) {
                dataSet.remove(ignoreMatch);
            }
        }

        System.out.println("基准宽度dp值：[ " + Tools.cutLastZero(baseDPI) + " dp ]");
        System.out.println("本次待适配的宽度dp值: [ " + Tools.getOrderedString(dataSet) + " ]");
        //获取基准的dimens.xml文件
        String baseDimenFilePath = params.getResFolderPath() + File.separator + "values" + File.separator + "dimens.xml";
        File testBaseDimenFile = new File(baseDimenFilePath);
        //判断基准文件是否存在
        if (!testBaseDimenFile.exists()) {
            System.out.println("DK WARNING:  \"./res/values/dimens.xml\" 路径下的文件找不到!");
            return "对应Module \"./res/values/dimens.xml\" 路径下的文件找不到!";
        }
        //解析源dimens.xml文件
        ArrayList<XMLItem> list = XmlIO.readDimenFile(baseDimenFilePath);
        if (list == null || list.size() <= 0) {
            System.out.println("DK WARNING:  \"./res/values/dimens.xml\" 文件无数据!");
            return "\"./res/values/dimens.xml\" 文件无数据!";
        } else {
            System.out.println("OK \"./res/values/dimens.xml\" 基准dimens文件解析成功!");
        }
        try {
            if (dataSet.isEmpty()) {
                System.out.println("DK WARNING:  \"未找到匹配的match_dp配置, 请检查配置文件设置\"");
                return "未找到匹配的match_dp配置, 请检查配置文件设置";
            }
            //循环指定的dp参数，生成对应的dimens-swXXXdp.xml文件
            for (double dpi : dataSet) {
                //获取当前dp除以baseDP后的倍数
                double multiple = dpi / baseDPI;

                //待输出的目录
                String outFolderPath;
                //待删除的目录
                String delFolderPath;
                //values目录上带的dp整数值
                String folderDP = String.valueOf((int) dpi);

                if (params.isCreateSmallestWithFolder()) {
                    outFolderPath = VALUES_NEW_FOLDER.replace(LETTER_REPLACE, folderDP);
                    delFolderPath = VALUES_OLD_FOLDER.replace(LETTER_REPLACE, folderDP);
                } else {
                    outFolderPath = VALUES_OLD_FOLDER.replace(LETTER_REPLACE, folderDP);
                    delFolderPath = VALUES_NEW_FOLDER.replace(LETTER_REPLACE, folderDP);
                }
                outFolderPath = params.getResFolderPath() + File.separator + outFolderPath + File.separator;
                delFolderPath = params.getResFolderPath() + File.separator + delFolderPath + File.separator;


                if (IS_DELETE_LEGACY_FOLDER) {
                    /**
                     * 删除以前适配方式的目录values-wXXXdp
                     */
                    File oldFile = new File(delFolderPath);
                    if (oldFile.exists() && oldFile.isDirectory() && Tools.isOldFolder(oldFile.getName(), params.isCreateSmallestWithFolder())) {
                        //找出res目录下符合要求的values目录，然后递归删除values目录
                        Tools.deleteFile(oldFile);
                    }
                }


                /**
                 * 生成新的目录values-swXXXdp
                 */
                //创建当前dp对应的dimens文件目录
                new File(outFolderPath).mkdirs();


                //生成的dimens文件的路径
                String outPutFile = outFolderPath + "dimens.xml";
                //生成目标文件dimens.xml输出目录
                XmlIO.createDestinationDimens(params, new ArrayList<>(list), multiple, outPutFile);
            }
            System.out.println("OK ALL OVER，全部生成完毕！");
            //适配完成
            return "Over, adapt successful";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}