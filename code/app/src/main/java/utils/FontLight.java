package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontLight extends TextView {

    public FontLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
   public FontLight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
   public FontLight(Context context) {
        super(context);
   }
   public void setTypeface(Typeface tf, int style) {
         
              super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Light-webfont.ttf"));
         
    }
}