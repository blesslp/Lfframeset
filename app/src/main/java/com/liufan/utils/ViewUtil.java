package com.liufan.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewUtil {

//
//	public static void mergeMultiListView(MultiColumnListView listView,int attrHeight) {
//		//获取ListView对应的Adapter
//		LBaseAdapter listAdapter = (LBaseAdapter) listView.getAdapter(); 
//		if (listAdapter == null) {
//			// pre-condition
//			return;
//		}
//		int totalHeight = 0;
//		int totalLeft = 0;
//		int totalRight = 0;
//		for (int i = 0, len = listAdapter.getCount(); i < len; i+=2) {   //listAdapter.getCount()返回数据项的数目
//			View listItem = listAdapter.getView(i, null, listView);
//			listItem.measure(0, 0);  //计算子项View 的宽高
//			final int leftHeight = listItem.getMeasuredHeight();  //统计所有子项的总高度
//			totalLeft += leftHeight;
//			int rightHeight = 0;
//			if(i+1 < len) {
//				listItem = listAdapter.getView(i+1, null, listView);
//				listItem.measure(-1, -1);  //计算子项View 的宽高
//				rightHeight = listItem.getMeasuredHeight();  //统计所有子项的总高度
//				totalRight += rightHeight;
//			}
//		}
//		totalHeight = Math.max(totalLeft, totalRight);
//		ViewGroup.LayoutParams params = listView.getLayoutParams();
//		params.height =(int) ((totalHeight + (listAdapter.getCount())*attrHeight));
//		//listView.getDividerHeight()获取子项间分隔符占用的高度
//		//params.height最后得到整个ListView完整显示需要的高度
//		listView.setLayoutParams(params);
//	}
	public static void setListViewHeightBasedOnChildren(ExpandableListView listView) {
		//获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter(); 
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   //listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);  //计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		//listView.getDividerHeight()获取子项间分隔符占用的高度
		//params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 在ScrollView嵌套ExpandableListView的时候，此方法能够使ExpandableListView自适应高度
	 * 
	 * @param listView
	 * @param attHeight
	 */
	public static void setExpandableListViewHeightBasedOnChildren(ExpandableListView listView,BaseExpandableListAdapter listAdapter,
			int attHeight,boolean isExpandable,int groupPosition) {
		if (listAdapter == null) {
			return;
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();

		int totalHeight = params.height;

		for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
			View listItem = listAdapter.getChildView(groupPosition, i, false, null, null);
			listItem.measure(0, 0);
			if(!isExpandable) {
				totalHeight -= listItem.getMeasuredHeight();
			}else {
				totalHeight += listItem.getMeasuredHeight();
			}
		}
		if(isExpandable) {
			attHeight *= 1;
		}else {
			attHeight *= -1;
		}
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getChildrenCount(groupPosition) - 1)) + attHeight;
		listView.setLayoutParams(params);
	}


	/**
	 * 在ScrollView嵌套ListView的时候，此方法能够使ListView自适应高度
	 * 
	 * @param listView
	 * @param attHeight
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView,
			int attHeight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter
						.getCount() - 1)) + attHeight;
		listView.setLayoutParams(params);
	}

	public static int getListViewHeightBasedOnChildren(ListView listView,
			int attHeight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		return totalHeight
				+ (listView.getDividerHeight() * (listAdapter
						.getCount() - 1)) + attHeight;
	}

	/**
	 * ListView中包含GridView时，gridView设置高度为wrap_content失效， 可以使用下面方法来实现自适应高度
	 */
	public static void setGridViewHeightBasedOnChildren(GridView gridView,
			int attHeight, int numberPerRow) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int totalCount = listAdapter.getCount();
		int rowCount;
		if (totalCount % numberPerRow == 0) {
			rowCount = totalCount / numberPerRow;
		} else {
			rowCount = totalCount / numberPerRow + 1;
		}
		for (int i = 0; i < rowCount; i++) {
			View listItem = listAdapter.getView(i, null, gridView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (listAdapter.getCount() - 1)
				+ attHeight;
		gridView.setLayoutParams(params);
	}

	/**
	 *	说明：设置PulltorefreshListView的高度
	 *	<hr>
	 *	@author		刘帆
	 *	@param listView
	 *	@param dip2px
	 *	@return
	 *	@return		int
	 *	
	 *----------------------------
	 *
	 */
	//	public static void setPulltoRefreshListViewHeightBasedOnChildren(
	//			PullToRefreshListView listView, int baseHeight) {
	//		LayoutParams layoutParams = listView.getLayoutParams();
	//		layoutParams.height = ViewUtil.getListViewHeightBasedOnChildren(listView.getRefreshableView(),baseHeight);
	//		listView.setLayoutParams(layoutParams);
	//	}
}