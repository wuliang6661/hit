package com.wul.hlt.widget;

import android.text.InputFilter;
import android.text.Spanned;

import com.blankj.utilcode.util.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wuliang on 2018/12/30.
 */

public class EditFilter implements InputFilter {

    Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_]");

    @Override
    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
        Matcher matcher = pattern.matcher(charSequence);
        if (!matcher.find()) {
            return null;
        } else {
            ToastUtils.showShort("只能输入汉字,英文，数字");
            return "";
        }

    }
}

