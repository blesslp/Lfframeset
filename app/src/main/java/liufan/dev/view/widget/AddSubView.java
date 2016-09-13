package liufan.dev.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import liufan.dev.lfframeset.R;


/**
 * Created by liufan on 15/12/9.
 */
public class AddSubView extends FrameLayout{
    private ImageButton mAdd;
    private ImageButton mSub;
    private EditText mEdit;
    private long mNum = 0;
    private int textSize;
    private int textColor;
    //允许值小于0，默认为不允许
    private boolean arrowLt0 = false;
    private OnNumberChangeListener l;

    public AddSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public AddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public AddSubView(Context context) {
        super(context);
        init(null);
    }

    public void setNumber(long number) {
        this.mNum = number;
        notifyChangeNum();
    }

    private void init(AttributeSet attrs) {
        View container = View.inflate(getContext(), R.layout.add_sub_view,this);
        mAdd = (ImageButton) container.findViewById(R.id.btnAdd);
        mSub = (ImageButton) container.findViewById(R.id.btnSub);
        mEdit = (EditText) container.findViewById(R.id.edit);
        notifyChangeNum();
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AddSubView);
            if(typedArray != null) {
                this.arrowLt0 = typedArray.getBoolean(R.styleable.AddSubView_allowlt0, false);
                this.textSize = typedArray.getDimensionPixelSize(R.styleable.AddSubView_textSize, 14);
                this.textColor = typedArray.getColor(R.styleable.AddSubView_textColor, Color.parseColor("#666666"));
                this.mNum = typedArray.getInt(R.styleable.AddSubView_number, 0);
                notifyChangeTextSize();
                notifyChangeTextColor();
                notifyChangeNum();
                typedArray.recycle();
                typedArray = null;
            }
        }
        initEvents();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        notifyChangeTextSize();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        notifyChangeTextColor();
    }

    private void notifyChangeTextColor() {
        mEdit.setTextColor(textColor);
    }

    private void notifyChangeTextSize() {
        mEdit.setTextSize(textSize);
    }


    private void initEvents(){
        mAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNum ++;
                notifyChangeNum();
            }
        });
        mSub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNum --;
                notifyChangeNum();
            }
        });
        mEdit.setSelectAllOnFocus(true);
        mEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mEdit.setSelection(0, mEdit.getText().length());
                }
            }
        });
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString().trim();
                if (TextUtils.isEmpty(temp)) {
                    mNum = 0;
                    notifyChangeNum();
                    return;
                }
                if (!arrowLt0) {
                    //不允许小于0
                    if (temp.startsWith("-") || temp.startsWith("+")) {
                        temp = temp.substring(1);
                        mEdit.setText(temp);
                        return;
                    }
                }
                if(temp.equals("-") || temp.startsWith("+")) {
                    mNum = 0;
                    notifyChangeNum();
                    return;
                }
                mNum = Long.valueOf(temp);
                if (l != null) {
                    l.onChange(mNum);
                }

            }
        });
    }

    public void setOnNumberChangedListener(OnNumberChangeListener l) {
        this.l = l;
    }

    private void notifyChangeNum() {
        if(!arrowLt0) {
            //不允许小于0
            mNum = mNum<0?0:mNum;
        }
        mEdit.setText(String.valueOf(mNum));
    }

    public long getNumber() {
        return this.mNum;
    }


    public interface OnNumberChangeListener{
        public void onChange(long num);
    }
}
