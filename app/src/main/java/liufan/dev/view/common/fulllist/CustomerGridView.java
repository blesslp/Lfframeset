package liufan.dev.view.common.fulllist;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.widget.GridView;
/** 
 *	说明：
 *	<hr>
 *	@author 刘帆
 *	@date 2015年6月30日 
 */
public class CustomerGridView extends GridView{

	public CustomerGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomerGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public CustomerGridView(Context context) {
		super(context);
	}
	
	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
				MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, expandSpec);  
	} 

}
