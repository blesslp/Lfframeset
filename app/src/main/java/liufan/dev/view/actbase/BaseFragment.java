package liufan.dev.view.actbase;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import liufan.dev.view.annotation.InjectLayout;
import liufan.dev.view.annotation.processor.InjectLayoutProcessor;
import liufan.dev.view.annotation.processor.InjectSrvProcessor;

/**
 * Created by liufan on 16/5/9.
 */
public abstract class BaseFragment extends Fragment {
    private LoadingDialog mLoadingDialog;

    private View _view;
    private boolean isVisible;
    private boolean isPrepared;
    private boolean isFirst = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(_view == null) {
            int contentViewId = InjectLayoutProcessor.process(this);
            if (contentViewId <= 0) {
                //没有指定XML布局
                throw new IllegalArgumentException("没有指定布局文件");
            }
            _view = inflater.inflate(contentViewId, container, false);
            InjectSrvProcessor.process(this);
        }else{
            final ViewParent parent = _view.getParent();
            if (parent != null) {
                ViewGroup vg = (ViewGroup)parent;
                vg.removeView(_view);
            }
        }
        return _view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        initPrepare();
    }

    protected abstract void initPrepare();

    protected abstract void onInvisible();

    public abstract void lazyinitData();

    protected abstract void onVisible();

    private void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        if (!isFirst) {
            onVisible();
        }else {
            lazyinitData();
            isFirst = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }

    private final void initLoadingDialog() {
        this.mLoadingDialog = new LoadingDialog(getActivity());
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        }else{
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            isVisible = false;
            onInvisible();
        }else{
            isVisible = true;
            lazyLoad();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {

            initLoadingDialog();
        }
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
        if (Build.VERSION.SDK_INT > 14) {   //仅在当前Fragment处于可见时，才显示
            if (getUserVisibleHint()) {
                if (!this.mLoadingDialog.isShowing()) {
                    this.mLoadingDialog.show(canCancel, canTouchCancel);
                }
                this.mLoadingDialog.setText(msg);
            }
        } else {
            if (!this.mLoadingDialog.isShowing()) {
                this.mLoadingDialog.show(canCancel, canTouchCancel);
            }
            this.mLoadingDialog.setText(msg);
        }
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
