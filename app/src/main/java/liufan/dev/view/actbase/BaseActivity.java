package liufan.dev.view.actbase;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import liufan.dev.view.annotation.processor.InjectBinderLayout;
import liufan.dev.view.annotation.processor.InjectLayoutProcessor;
import liufan.dev.view.annotation.processor.InjectSrvProcessor;

/**
 * Created by liufan on 16/5/9.
 */

public class BaseActivity extends AppCompatActivity {
    private LoadingDialog mLoadingDialog;
    private View baseView;
    private ViewDataBinding viewDataBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutID = InjectLayoutProcessor.process(this);
        if (layoutID != -1) {
            setContentView(layoutID);
        } else {
            layoutID = InjectBinderLayout.process(this);
            if (layoutID != -1) {
                this.viewDataBinding = DataBindingUtil.setContentView(this, layoutID);
            } 
        }
        baseView = getWindow().getDecorView();
        InjectSrvProcessor.process(this);
        initLoadingDialog();
    }


    private final void initLoadingDialog() {
        this.mLoadingDialog = new LoadingDialog(this);
    }

    public View getBaseView() {
        return baseView;
    }

    public <T extends ViewDataBinding> T getBinder() {
        if (this.viewDataBinding == null) {
            throw new NullPointerException("用InjectBinderLayout注解才可以使用此方法");
        }
        return (T)this.viewDataBinding;
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeLoading();
    }

    /**
     * 显示一个等待对话框<br>
     *
     * @param msg            进度对话框的提示文字
     * @param canCancel      true:可以响应返回键关闭, false:不可
     * @param canTouchCancel true:可以点击对话框以外区域关闭, false:不可
     * @return LoadingDialog 返回一个对话框对象，用于在不关闭对话框的情况下修改对话框文字 <a href="#">setText(String)</a>
     */
    protected LoadingDialog showLoadingDialog(String msg, boolean canCancel, boolean canTouchCancel) {
        if (!this.mLoadingDialog.isShowing()) {
            this.mLoadingDialog.show(canCancel, canTouchCancel);
        }
        this.mLoadingDialog.setText(msg);
        return this.mLoadingDialog;
    }

    /**
     * 关闭正在显示的对话框
     */
    protected void closeLoading() {
        if (this.mLoadingDialog.isShowing())
            this.mLoadingDialog.dismiss();
    }
}
