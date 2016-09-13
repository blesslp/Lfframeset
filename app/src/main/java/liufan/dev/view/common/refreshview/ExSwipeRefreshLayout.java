package liufan.dev.view.common.refreshview;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import liufan.dev.lfframeset.R;


/**
 * Created by liufan on 16/4/14.
 */
public class ExSwipeRefreshLayout extends SwipeRefreshLayout {

    private View loadMore;
    private ProgressBar loadMoreBar;
    private TextView loadMoreTxt;
    private ListView listView;
    private AtomicInteger pageNow = new AtomicInteger(0);

    public ExSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ExSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setup(ListView view) {
        this.listView = view;
        initView();
    }

    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
    }

    private void initView() {

        setColorSchemeColors(Color.BLUE, Color.GREEN, Color.YELLOW);

        this.listView.setOnScrollListener(new ScrollListener());
        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNow.set(tempPageConfig);
                isLoadMore = true;
                if (or != null) {
                    or.onRefresh(ExSwipeRefreshLayout.this);
                }
            }
        });

        this.loadMore = View.inflate(getContext(), R.layout.loading_more, null);
        this.loadMore.setVisibility(View.GONE);
        this.loadMoreBar = (ProgressBar) loadMore.findViewById(R.id.loadBar);
        this.loadMoreTxt = (TextView) loadMore.findViewById(R.id.loadTxt);
        this.listView.addFooterView(this.loadMore,null,false);
//        View noDataView = findViewById(R.id.viewNoData);
//        if (noDataView != null) {
//            this.listView.setEmptyView(noDataView);
//            noDataView.findViewById(R.id.btnRefresh).setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pageNow.set(tempPageConfig);
//                    if (or != null) {
//                        or.onRefresh(ExSwipeRefreshLayout.this);
//                    }
//                }
//            });
//        }
    }

    ;

    public interface OnRefreshListener {
        public void onRefresh(final ExSwipeRefreshLayout view);
    }

    public interface OnLoadMoreListener {
        public void onLoadMore(final ExSwipeRefreshLayout view, int currenPage);
    }

    private OnRefreshListener or;
    private OnLoadMoreListener ol;

    public void setOnLoadMoreListener(OnLoadMoreListener ol) {
        this.ol = ol;
    }

    public void setOnExRefreshListener(OnRefreshListener or) {
        this.or = or;
    }

    /**
     * 当前分页，默认是从0开始
     *
     * @param pageNow
     */
    private int tempPageConfig = 0;

    public void configInitPageNum(int pageNow) {
        this.tempPageConfig = pageNow;
        this.pageNow.set(pageNow);
    }


    public int getPageNow() {
        return pageNow.intValue();
    }

    public interface OnScrollListener {
        public void onScrollStateChanged(AbsListView view, int scrollState);

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    private boolean isLoadMore = true;

    public void setLoadResult(boolean isOk, String msg) {
        if (!isOk) {
            //加载失败的话，分页减一
            if (listView.getAdapter().isEmpty() || (listView.getFirstVisiblePosition() == 0 && listView.getLastVisiblePosition() <= listView.getAdapter().getCount()) ) {
                this.loadMore.setVisibility(View.GONE);
                isLoadMore = false;
            }else{
                this.loadMore.setVisibility(View.VISIBLE);
                this.loadMoreTxt.setText(msg);
                this.loadMoreBar.setVisibility(View.GONE);
                isLoadMore = false;

            }

        } else {
            this.loadMore.setVisibility(View.GONE);
            isLoadMore = true;
//            this.listView.removeFooterView(this.loadMore);
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            isLoadMore = true;
            pageNow.set(tempPageConfig);
        }
        super.setRefreshing(refreshing);

    }

    private void resetLoadMore() {
//        this.listView.removeFooterView(this.loadMore);
//        this.listView.addFooterView(this.loadMore, null, false);
        this.loadMoreTxt.setText("正在努力加载中..");
        this.loadMoreBar.setVisibility(View.VISIBLE);
        this.loadMore.setVisibility(View.VISIBLE);
        isLoadMore = true;
    }

    public void enableLoadMore(boolean enable) {
        this.isLoadMore = !enable;
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    private List<OnScrollListener> onScrollListeners = new ArrayList<>();

    /**
     * 如果要给ListView加滑动监听来处理你的业务，应当使用这个，不然会上拉分页会失效
     *
     * @param onScrollListener
     */
    public void addOnListViewScrollListener(OnScrollListener onScrollListener) {
        onScrollListeners.add(onScrollListener);
    }

    class ScrollListener implements AbsListView.OnScrollListener {
        private boolean isBottom = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            if (isBottom && scrollState == SCROLL_STATE_IDLE && isLoadMore) {
                if (ol != null) {
                    resetLoadMore();
                    ol.onLoadMore(ExSwipeRefreshLayout.this, pageNow.incrementAndGet());
                }
            }
//            isTouchScroll = scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
            for (OnScrollListener on : onScrollListeners) {
                on.onScrollStateChanged(view, scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            isBottom = (visibleItemCount + firstVisibleItem == totalItemCount && firstVisibleItem != 0);
            for (OnScrollListener on : onScrollListeners) {
                on.onScroll(view, firstVisibleItem,visibleItemCount,totalItemCount);
            }
//            if (!isLoadMore && isTouchScroll && firstVisibleItem > 0 && (visibleItemCount + firstVisibleItem == totalItemCount)) {
//                if (ol != null) {
//                    resetLoadMore();
//                    ol.onLoadMore(ExSwipeRefreshLayout.this, pageNow.incrementAndGet());
//                }
//            }
//            setEnabled(firstVisibleItem==0);
        }
    }

}
