package com.udacity.android.popularmovies.ui;


import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import com.squareup.picasso.Transformation;

public class MaskTransformation implements Transformation {

    private static Paint mMaskingPaint = new Paint();

    static {
        mMaskingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    private Context mContext;
    private int mMaskId;

    /**
     * @param maskId If you change the mask file, please also rename the mask file, or Glide will get
     *               the cache with the old mask. Because getId() return the same values if using the
     *               same make file name. If you have a good idea please tell us, thanks.
     */
    public MaskTransformation(Context context, int maskId) {
        mContext = context.getApplicationContext();
        mMaskId = maskId;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Drawable mask = Utils.getMaskDrawable(mContext, mMaskId);

        Canvas canvas = new Canvas(result);
        mask.setBounds(0, 0, width, height);
        mask.draw(canvas);
        canvas.drawBitmap(source, 0, 0, mMaskingPaint);

        source.recycle();

        return result;
    }

    @Override
    public String key() {
        return "MaskTransformation(maskId=" + mContext.getResources().getResourceEntryName(mMaskId)
                + ")";
    }

    private static class Utils {

        private Utils() {
        }

        public static Drawable getMaskDrawable(Context context, int maskId) {
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getDrawable(maskId);
            } else {
                drawable = context.getResources().getDrawable(maskId);
            }

            if (drawable == null) {
                throw new IllegalArgumentException("maskId is invalid");
            }

            return drawable;
        }
    }
}
