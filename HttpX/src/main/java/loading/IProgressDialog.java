package loading;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class IProgressDialog extends ProgressDialog implements ILoadingI {
    public IProgressDialog(Context context) {
        super(context);
    }


    @Override
    public Activity getAct() {
        return null;
    }

    @Override
    public void beginShow() {
        if (!isShowingI()) {
            show();
        }
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
