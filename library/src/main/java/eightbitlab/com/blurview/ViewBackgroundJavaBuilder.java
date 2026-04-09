package eightbitlab.com.blurview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ViewBackgroundJavaBuilder {

    /**
     * 圆角
     */
    public static abstract class CornerRadius {
        public static class AllCornerRadius extends CornerRadius {
            public final float size;

            public AllCornerRadius(float size) {
                this.size = size;
            }

            @Override
            public float size() {
                return size;
            }
        }

        public static class EachCornerRadius extends CornerRadius {
            public final float topLeft, topRight, bottomLeft, bottomRight;

            public EachCornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
                this.topLeft = topLeft;
                this.topRight = topRight;
                this.bottomLeft = bottomLeft;
                this.bottomRight = bottomRight;
            }

            public float[] convert() {
                return new float[]{
                        topLeft, topLeft,
                        topRight, topRight,
                        bottomLeft, bottomLeft,
                        bottomRight, bottomRight
                };
            }

            @Override
            public float size() {
                return Math.max(topLeft, topRight);
            }
        }

        public abstract float size();
    }

    // private int mShape = -1;
    // private float mAlpha = -1f;
    CornerRadius mCorner = null;
    private float mStrokeWidth = 0f;
    private int mStrokeColor = 0;
    private ColorStateList mBg = null;
    private int mGradientStartColor = 0;
    private int mGradientEndColor = 0;
    private int mGradientAngle = 0;

    private int mBgAlpha = -1;

    public boolean isAtLeastOne = false;

    /**
     * 0~3 RECT, OVAL, LINE, RING
     */
//    public ViewBackgroundJavaBuilder setShape(int shape) {
//        mShape = shape;
//        return this;
//    }
//
//    public ViewBackgroundJavaBuilder setAlpha(float alpha) {
//        mAlpha = alpha;
//        return this;
//    }

    public ViewBackgroundJavaBuilder setStroke(float width, int color) {
        if (width > 0) {
            mStrokeWidth = width;
            mStrokeColor = color;
            isAtLeastOne = true;
        }
        return this;
    }

    public ViewBackgroundJavaBuilder setCornerRadius(float cornerRadius) {
        if (cornerRadius > 0) {
            mCorner = new CornerRadius.AllCornerRadius(cornerRadius);
            isAtLeastOne = true;
        }
        return this;
    }

    public ViewBackgroundJavaBuilder setCornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        if (topLeft > 0f || topRight > 0f || bottomLeft > 0f || bottomRight > 0f) {
            mCorner = new CornerRadius.EachCornerRadius(topLeft, topRight, bottomLeft, bottomRight);
            isAtLeastOne = true;
        }
        return this;
    }

    public ViewBackgroundJavaBuilder setBackgroundAlpha(int alpha) {
        mBgAlpha = alpha;
        return this;
    }

    /**
     * 设置线性渐变背景
     * @param startColor 渐变开始颜色
     * @param endColor 渐变结束颜色
     * @param angle 渐变角度。虽然支持任意 int 值，但内部会映射到最接近的 45 度倍数方向：
     *              0: LEFT_RIGHT (左 -> 右)
     *              45: BL_TR (左下 -> 右上)
     *              90: BOTTOM_TOP (下 -> 上)
     *              135: BR_TL (右下 -> 左上)
     *              180: RIGHT_LEFT (右 -> 左)
     *              225: TR_BL (右上 -> 左下)
     *              270: TOP_BOTTOM (上 -> 下)
     *              315: TL_BR (左上 -> 右下)
     */
    public ViewBackgroundJavaBuilder setGradient(int startColor, int endColor, int angle) {
        if (startColor != 0 && endColor != 0) {
            mGradientStartColor = startColor;
            mGradientEndColor = endColor;
            mGradientAngle = angle;
            isAtLeastOne = true;
        }
        return this;
    }

    public ViewBackgroundJavaBuilder setBackground(int color) {
        return setBackground(color, 0, 0);
    }

    public ViewBackgroundJavaBuilder setBackground(int color, int pressedColor, int disabledColor) {
        class ColorData {
            int[] state;
            int color;

            ColorData(int[] state, int color) {
                this.state = state;
                this.color = color;
            }
        }

        List<ColorData> colorMap = new ArrayList<>();
        int noColor = 0;

        boolean hasColor = false;
        if (pressedColor != noColor) {
            colorMap.add(new ColorData(new int[]{android.R.attr.state_pressed}, pressedColor));
            hasColor = true;
        }
        if (disabledColor != noColor) { //-代表false
            colorMap.add(new ColorData(new int[]{-android.R.attr.state_enabled}, disabledColor));
            hasColor = true;
        }
        if (color != noColor) {
            colorMap.add(new ColorData(new int[]{0}, color));
            hasColor = true;
        }

        if (hasColor) {
            int size = colorMap.size();
            int[][] stateArray = new int[size][];
            int[] colorArray = new int[size];
            for (int i = 0; i < size; i++) {
                stateArray[i] = colorMap.get(i).state;
                colorArray[i] = colorMap.get(i).color;
            }
            mBg = new ColorStateList(stateArray, colorArray);
            isAtLeastOne = true;
        }

        return this;
    }

    public Drawable build(Context context) {
        if (!isAtLeastOne) {
            return null;
        }

        GradientDrawable it = new GradientDrawable();
        //背景
        if (mGradientStartColor != 0 && mGradientEndColor != 0) {
            it.setColors(new int[]{mGradientStartColor, mGradientEndColor});
            it.setOrientation(getGradientOrientation(mGradientAngle));
            it.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        } else if (mBg != null) {
            it.setColor(mBg);
        }

        //圆角
        if (mCorner instanceof CornerRadius.AllCornerRadius) {
            it.setCornerRadius(((CornerRadius.AllCornerRadius) mCorner).size);
        } else if (mCorner instanceof CornerRadius.EachCornerRadius) {
            it.setCornerRadii(((CornerRadius.EachCornerRadius) mCorner).convert());
        }

        //边框
        if (mStrokeWidth > 0 && mStrokeColor != 0) {
            it.setStroke((int) mStrokeWidth, mStrokeColor);
        }

        //形状 RECTANGLE, OVAL, LINE, RING
//        switch (mShape) {
//            case 0: it.setShape(GradientDrawable.RECTANGLE); break;
//            case 1: it.setShape(GradientDrawable.OVAL); break;
//            case 2: it.setShape(GradientDrawable.LINE); break;
//            case 3: it.setShape(GradientDrawable.RING); break;
//        }

        //alpha
        if (mBgAlpha >= 0) {
            it.setAlpha(mBgAlpha);
        }
        return it;
    }

    private GradientDrawable.Orientation getGradientOrientation(int angle) {
        int normalizedAngle = ((angle % 360) + 360) % 360;
        int index = (int) ((normalizedAngle + 22.5) / 45) % 8;
        switch (index) {
            case 0: return GradientDrawable.Orientation.LEFT_RIGHT; // 0
            case 1: return GradientDrawable.Orientation.BL_TR;      // 45
            case 2: return GradientDrawable.Orientation.BOTTOM_TOP; // 90
            case 3: return GradientDrawable.Orientation.BR_TL;      // 135
            case 4: return GradientDrawable.Orientation.RIGHT_LEFT; // 180
            case 5: return GradientDrawable.Orientation.TR_BL;      // 225
            case 6: return GradientDrawable.Orientation.TOP_BOTTOM; // 270
            case 7: return GradientDrawable.Orientation.TL_BR;      // 315
            default: return GradientDrawable.Orientation.LEFT_RIGHT;
        }
    }

    public float[] getCornerRadiiArray() {
        if (mCorner instanceof CornerRadius.AllCornerRadius) {
            float r = ((CornerRadius.AllCornerRadius) mCorner).size;
            return new float[]{r, r, r, r, r, r, r, r};
        } else if (mCorner instanceof CornerRadius.EachCornerRadius) {
            return ((CornerRadius.EachCornerRadius) mCorner).convert();
        } else {
            return new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        }
    }

    /**
     * 对任何view设置RippleColor颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Drawable setRippleColor(Drawable drawable, @ColorInt int rippleColor, Integer radius) {
        if (drawable instanceof RippleDrawable) {
            ((RippleDrawable) drawable).setColor(ColorStateList.valueOf(rippleColor));
            if (radius != null) {
                ((RippleDrawable) drawable).setRadius(radius);
            }
            return drawable;
        }

        RippleDrawable newDrawable = new RippleDrawable(
                ColorStateList.valueOf(rippleColor),
                drawable,
                null
        );
        if (radius != null) {
            newDrawable.setRadius(radius);
        }
        return newDrawable;
    }

    /**
     * 对任何view设置RippleColor颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setRippleColor(View view, @ColorInt int rippleColor, Integer radius) {
        Drawable drawable = view.getForeground() != null ? view.getForeground() : view.getBackground();
        Drawable fixDrawable = setRippleColor(drawable, rippleColor, radius);
        if (view.getForeground() != null) {
            view.setForeground(fixDrawable);
        } else {
            view.setBackground(fixDrawable);
        }
    }
}
