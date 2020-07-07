package com.duke.screenmatch.dp;

import com.duke.screenmatch.settings.SettingsParams;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;

/**
 * @Author: duke
 * @DateTime: 2016-08-24 17:32
 * @UpdateTime: 2020-07-07 17:54
 * @Description: dimens文件中的document comment数据项
 */
public class DocumentCommentItem extends CommentItem {

    @Override
    public void transfer(TransformerHandler handler,
                         AttributesImpl attributes,
                         SettingsParams params) throws SAXException {
        if (!params.isKeepSourceCodeComments()) return;

        newLine(handler)
                .comment(handler, characters)
                .newLine(handler)
                .newLine(handler);
    }
}
