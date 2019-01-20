package com.wul.hlt.ui.login;

import com.wul.hlt.api.HttpServiceIml;
import com.wul.hlt.entity.UserBo;
import com.wul.hlt.mvp.BasePresenterImpl;

import rx.Subscriber;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class LoginPresenter extends BasePresenterImpl<LoginContract.View> implements LoginContract.Presenter {

    @Override
    public void login(String phone, String shopNum, String password) {
        HttpServiceIml.login(shopNum, phone, password).subscribe(new Subscriber<UserBo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (mView != null) {
                    mView.onRequestError(e.getMessage());
                }
            }

            @Override
            public void onNext(UserBo userBo) {
                if (mView != null) {
                    mView.loginSuress(userBo);
                }
            }
        });
    }
}
