package com.wul.hlt.ui.login;

import com.wul.hlt.entity.UserBo;
import com.wul.hlt.mvp.BasePresenter;
import com.wul.hlt.mvp.BaseRequestView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginContract {
    interface View extends BaseRequestView {

        void loginSuress(UserBo userBo);

    }

    interface Presenter extends BasePresenter<View> {

        void login(String phone, String shopNum, String password);
    }
}
