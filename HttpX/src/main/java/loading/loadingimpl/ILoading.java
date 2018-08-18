package loading.loadingimpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.vise.log.Logger;

import loading.ILoadingI;

/**
 * 作者：马俊
 * 时间：2018/3/13 下午12:01
 * 邮箱：747673016@qq.com
 */

public class ILoading extends AlertDialog implements ILoadingI {
    public ILoading(@NonNull Context context) {
        super(context);
    }

    @Override
    public Activity getAct() {
//        return getOwnerActivity();
        Context context = getContext();
        Logger.e("loading", "##" + context);
        if (context != null && context instanceof Activity) {
            Logger.e("loading", "#获取Activity成功#" + context);
            return (Activity) context;
        } else {
            Logger.e("loading", "#获取Activity失败#");
            return null;
        }
    }

    @Override
    public void beginShow() {
        if (getAct() != null && !getAct().isFinishing()) {
        } else {
            System.out.println("dead~");
        }
        show();
    }

    @Override
    public void beginDismiss() {
        dismiss();
    }

    @Override
    public boolean isShowingI() {
        return isShowing();
    }
}
