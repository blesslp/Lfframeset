package liufan.dev.view.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.okhttp.internal.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liufan on 16/5/9.
 */
public abstract class LBaseAdapter<Element,VH extends LBaseAdapter.ViewHolder> extends BaseAdapter {
    private List<Element> _data = new ArrayList<>();
    private Context _context;

    public LBaseAdapter(Context context) {
        this._context = context;
    }

    public Context getContext() {
        return _context;
    }

    @Override
    public int getCount() {
        return _data.size();
    }

    public boolean setDataSource(List<Element> dataSource, boolean isClear) {
        if(isClear) _data.clear();
        if (dataSource == null || dataSource.size() == 0) {
            notifyDataSetChanged();
            return false;
        }
        _data.addAll(dataSource);
        notifyDataSetChanged();
        return true;
    }

    public List<Element> getDateSource() {
        return _data;
    }

    public boolean setDataSource(List<Element> dataSource) {
        return setDataSource(dataSource, true);
    }

    public void removeItem(int position) {
        final Element remove = _data.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Element item) {
        if (item != null) {
            _data.add(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public Element getItem(int position) {
        return _data.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract VH createViewHolder(ViewGroup parent,final int position);

    public abstract void bindViewHolder(final VH holder, final Element data, final int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if (convertView == null) {
            vh = createViewHolder(parent, position);
            if (vh == null) {
                throw new NullPointerException("createViewHolder不能返回null");
            }
            vh.getConvertView().setTag(vh);
        }else{
            vh = (VH) convertView.getTag();
        }
        vh.setPosition(position); // 绑定当前下标
        bindViewHolder(vh,getItem(position),position);
        return vh.getConvertView();
    }

    public static class ViewHolder {
        private ArrayMap<Integer, View> viewCache = new ArrayMap<>(5);
        private int currentPosition = -1;
        private View convertView;
        private Context _context;
        private Object obj;

        public void setTag(Object obj) {
            this.obj = obj;
        }

        public <R> R getTag() {
            return (R) obj;
        }

        public Context getContext() {
            return convertView.getContext();
        }

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public View getConvertView() {
            return convertView;
        }

        void setPosition(int pos) {
            this.currentPosition = pos;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }

        public <T> T get(@IdRes final int viewID) {
            if(!viewCache.containsKey(viewID)) {
                viewCache.put(viewID, convertView.findViewById(viewID));
            }
            return (T) viewCache.get(viewID);
        }
    }

}
