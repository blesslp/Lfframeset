package liufan.dev.view.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.liufan.xhttp.common.Entity;

/**
 * Created by liufan on 16/5/9.
 */
public class LBinderAdapter<T> extends LBaseAdapter<T,LBaseAdapter.ViewHolder> {
    private int layoutID =-1;
    private int BR_ID = -1;
    public LBinderAdapter(Context context, @LayoutRes int layout, int BR_ID) {
        super(context);
        this.layoutID = layout;
        this.BR_ID = BR_ID;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent, int position) {
        final ViewDataBinding inflate = DataBindingUtil.inflate(LayoutInflater.from(getContext()), layoutID, parent, false);
        final ViewHolder viewHolder = new ViewHolder(inflate.getRoot());
        viewHolder.setTag(inflate);
        return viewHolder;
    }

    @Override
    public void bindViewHolder(ViewHolder holder, T data, int position) {
        ViewDataBinding binder = holder.getTag();
        binder.setVariable(BR_ID, data);
        binder.executePendingBindings();
    }
}
