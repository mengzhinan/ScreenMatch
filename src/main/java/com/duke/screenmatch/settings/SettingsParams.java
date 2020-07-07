package com.duke.screenmatch.settings;

public class SettingsParams {

    /**
     * 字体是否也适配(是否与dp尺寸一样等比缩放)
     */
    private final boolean isFontMatch;

    /**
     * 基准dpi值
     */
    private final double baseDpi;

    /**
     * 待适配宽度dp值
     */
    private final double[] preferMatchDPIs;

    /**
     * 待忽略宽度dp值
     */
    private final double[] ignoreMatchDPIs;

    /**
     * base dimens.xml 文件的res目录
     */
    private final String resFolderPath;

    /**
     * 是否创建 values-swXXXdp 新格式的目录
     */
    private final boolean createSmallestWithFolder;

    /**
     * 是否保留源文件格式(注释 + 空行之类的)
     */
    private final boolean keepSourceCodeComments;

    /**
     * 是否生成默认的dpi适配列表
     */
    private final boolean addDefaultMatchDPIs;

    /**
     * dpi 缩放倍数
     */
    private final double multipleForDpi;

    private SettingsParams(boolean isFontMatch,
                           double baseDpi,
                           double[] preferMatchDPIs,
                           double[] ignoreMatchDPIs,
                           String resFolderPath,
                           boolean createSmallestWithFolder,
                           boolean keepSourceCodeComments,
                           boolean addDefaultMatchDPIs,
                           double multipleForDpi) {
        this.isFontMatch = isFontMatch;
        this.baseDpi = baseDpi;
        this.preferMatchDPIs = preferMatchDPIs;
        this.ignoreMatchDPIs = ignoreMatchDPIs;
        this.resFolderPath = resFolderPath;
        this.createSmallestWithFolder = createSmallestWithFolder;
        this.keepSourceCodeComments = keepSourceCodeComments;
        this.addDefaultMatchDPIs = addDefaultMatchDPIs;
        this.multipleForDpi = multipleForDpi;
    }

    public static class Builder {

        private boolean isFontMatch = true;
        private double baseDpi = 360;
        private double[] preferMatchDPIs;
        private double[] ignoreMatchDPIs;
        private String resFolderPath;
        private boolean createSmallestWithFolder = true;
        private boolean keepSourceCodeComments = true;
        private boolean addDefaultMatchDPIs = false;
        private double multipleForDpi = 1.0;

        /**
         * 字体是否也适配(是否与dp尺寸一样等比缩放)
         */
        public Builder setFontMatch(boolean fontMatch) {
            isFontMatch = fontMatch;
            return this;
        }

        /**
         * 基准dpi值
         */
        public Builder setBaseDpi(double baseDpi) {
            this.baseDpi = baseDpi;
            return this;
        }

        /**
         * 待适配宽度dpi值
         */
        public Builder setPreferMatchDPIs(double[] preferMatchDPIs) {
            this.preferMatchDPIs = preferMatchDPIs;
            return this;
        }

        /**
         * 待忽略宽度dpi值
         */
        public Builder setIgnoreMatchDPIs(double[] ignoreMatchDPIs) {
            this.ignoreMatchDPIs = ignoreMatchDPIs;
            return this;
        }

        /**
         * base dimens.xml 文件的res目录
         */
        public Builder setResFolderPath(String resFolderPath) {
            this.resFolderPath = resFolderPath;
            return this;
        }

        /**
         * 是否创建 values-swXXXdp 新格式的目录
         */
        public Builder setCreateSmallestWithFolder(boolean createSmallestWithFolder) {
            this.createSmallestWithFolder = createSmallestWithFolder;
            return this;
        }

        /**
         * 是否保留源文件格式(注释 + 空行之类的)
         */
        public Builder setKeepSourceCodeComments(boolean keepSourceCodeComments) {
            this.keepSourceCodeComments = keepSourceCodeComments;
            return this;
        }

        /**
         * 是否生成默认的dpi适配列表
         */
        public Builder setAddDefaultMatchDPIs(boolean addDefaultMatchDPIs) {
            this.addDefaultMatchDPIs = addDefaultMatchDPIs;
            return this;
        }

        /**
         * dpi 缩放倍数
         */
        public Builder setMultipleForDpi(double multipleForDpi) {
            this.multipleForDpi = multipleForDpi;
            return this;
        }

        public SettingsParams build() {
            return new SettingsParams(
                    isFontMatch,
                    baseDpi,
                    preferMatchDPIs,
                    ignoreMatchDPIs,
                    resFolderPath,
                    createSmallestWithFolder,
                    keepSourceCodeComments,
                    addDefaultMatchDPIs,
                    multipleForDpi);
        }
    }

    public Builder newBuilder() {
        return new Builder()
                .setFontMatch(this.isFontMatch)
                .setBaseDpi(this.baseDpi)
                .setPreferMatchDPIs(this.preferMatchDPIs)
                .setIgnoreMatchDPIs(this.ignoreMatchDPIs)
                .setResFolderPath(this.resFolderPath)
                .setCreateSmallestWithFolder(this.createSmallestWithFolder)
                .setKeepSourceCodeComments(this.keepSourceCodeComments)
                .setAddDefaultMatchDPIs(this.addDefaultMatchDPIs)
                .setMultipleForDpi(this.multipleForDpi);
    }

    public boolean isFontMatch() {
        return isFontMatch;
    }

    public double getBaseDpi() {
        return baseDpi;
    }

    public double[] getPreferMatchDPIs() {
        return preferMatchDPIs;
    }

    public double[] getIgnoreMatchDPIs() {
        return ignoreMatchDPIs;
    }

    public String getResFolderPath() {
        return resFolderPath;
    }

    public boolean isCreateSmallestWithFolder() {
        return createSmallestWithFolder;
    }

    public boolean isKeepSourceCodeComments() {
        return keepSourceCodeComments;
    }

    public boolean isAddDefaultMatchDPIs() {
        return addDefaultMatchDPIs;
    }

    public double getMultipleForDpi() {
        return multipleForDpi;
    }
}
