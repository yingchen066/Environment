package egova.com.cn.environment.login;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;

import egova.com.cn.environment.core.api.EgovaApi;
import egova.com.cn.environment.core.models.SoapEnvelop;
import egova.com.cn.environment.di.PerActivity;
import egova.com.cn.environment.util.CommonResult;
import egova.com.cn.environment.util.XmlResultProcessorNew;
import okhttp3.ResponseBody;
import rx.Subscriber;

@PerActivity
public class LoginPresenter {

    private final EgovaApi loginService;

    private final XmlResultProcessorNew xmlProcessor;

    @Inject
    public LoginPresenter(EgovaApi loginService, XmlResultProcessorNew xmlProcessor) {
        this.loginService = loginService;
        this.xmlProcessor = xmlProcessor;
    }

    private final Subscriber<ResponseBody> subscriber = new Subscriber<ResponseBody>() {
        @Override
        public void onCompleted() {
            System.out.println("complete");
            view.dismissLoginProgress();
        }

        @Override
        public void onError(Throwable e) {
            view.showErrors(e.getMessage());
            view.dismissLoginProgress();
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            try {
                String response = responseBody.string();
                CommonResult result = xmlProcessor.convert(response);
                if(result == null) {
                    view.showErrors("handle failed for response:" + response);
                    return;
                }
                if (result.getErrorCode() == CommonResult.CODE_SUCCESS) {
                    view.navigateToMainView();
                }else{
                    view.showErrors(result.getErrorDesc());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private LoginView view;

    public void setView(LoginView view) {
        this.view = view;
    }

    public void initialize() {
        view.hideErrors();
    }

    public void doLogin() {
        String username = view.getUsername();
        String password = view.getPassword();
        check(username, password);
        view.hideErrors();
        view.showLoginProgress();
        loginService.request(getSoapEnvelop())
//                .subscribeOn(Schedulers.io()io)
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @NonNull
    private SoapEnvelop getSoapEnvelop() {
        String body = "";
        SoapEnvelop soapEnvelop = new SoapEnvelop(body);
        return soapEnvelop;
    }

    private void check(String username, String password) {

    }
}
