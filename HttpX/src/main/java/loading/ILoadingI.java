package loading;

import android.app.Activity;

/**
 */

public interface ILoadingI {
    public Activity getAct();

    public void beginShow();

    public void beginDismiss();

    /**
     * 是否在loading?
     *
     * @return
     */
    public boolean isShowingI();
}

