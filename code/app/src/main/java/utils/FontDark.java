package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontDark extends TextView{
	 public FontDark(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	    }
	   public FontDark(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }
	   public FontDark(Context context) {
	        super(context);
	   }
	   public void setTypeface(Typeface tf, int style) {
	         
	              super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular-webfont.ttf"));
	         
	    }

}

