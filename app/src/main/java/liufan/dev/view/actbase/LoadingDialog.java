package liufan.dev.view.actbase;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import liufan.dev.lfframeset.R;


public class LoadingDialog extends Dialog{

	public LoadingDialog(Context context) {
		super(context, R.style.progress_style_dialog);
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		spaceshipImage = (ImageView) v.findViewById(R.id.img);
		tipTextView = (TextView) v.findViewById(R.id.tipTextView);
		hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,R.anim.loading_animation);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		setContentView(
				layout,
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT));
	}
	private Animation hyperspaceJumpAnimation;
	private ImageView spaceshipImage;
	private TextView tipTextView;

	@Override
	public void addContentView(View view, LayoutParams params) {
		super.addContentView(view, params);
	}


	public void setText(String msg) {
		tipTextView.setText(msg);
	}
	
	public void show(boolean canCancel,boolean canTouchCancel) {
		this.setCancelable(canCancel);
		this.setCanceledOnTouchOutside(canCancel);
		show();
	}
	
	
	@Override
	public void show() {
		super.show();
		startAnimation();
	}
	
	private void startAnimation(){
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
	}



}
