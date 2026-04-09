package eightbitlab.com.blurview;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

/**
 * 模糊背景
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class BlurView3Util {

    private final BlurView blurView;
    private final float blurRadius;
    
    public final boolean isLegacy;
    public final float cornerRadiusDp;
    public final ViewOutlineProvider viewOutlineProvider;

    /**
     * @param blurView BlurView
     * @param cornerRadius BlurView的圆角, 注意不需要 dp转换，内部处理。
     * @param blurRadius 模糊半径
     */
    public BlurView3Util(BlurView blurView, int cornerRadius, float blurRadius) {
        this.blurView = blurView;
        this.blurRadius = blurRadius;
        
        this.isLegacy = Build.VERSION.SDK_INT < Build.VERSION_CODES.S;
        
        this.cornerRadiusDp = blurView.getContext().getResources().getDisplayMetrics().density * cornerRadius;

        this.viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadiusDp);
            }
        };
    }

    /**
     * 设置常规模糊
     *
     * @param target 模糊目标
     * @param legacyBgColor 过时的替代颜色
     */
    public void setBlur(BlurTarget target, int legacyBgColor) {
        if (!isLegacy) {
            blurView.setupWith(target)
                    .setBlurRadius(blurRadius);
            blurView.setOutlineProvider(viewOutlineProvider);
            blurView.setClipToOutline(true);
        } else {
            Drawable bg = new ViewBackgroundJavaBuilder()
                    .setBackground(legacyBgColor)
                    .setCornerRadius(cornerRadiusDp)
                    .build(blurView.getContext());
            if (bg != null) {
                blurView.setBackground(bg);
            }
        }
    }

    /**
     * 设置渐进模糊
     *
     * @param target 模糊目标
     * @param direction 模糊方向
     * @param applyNoise 是否应用噪点，推荐false，否则会有明显的分界线
     * @param legacyBgStartColor 过时的替代开始颜色
     * @param legacyBgEndColor 过时的替代结束颜色
     */
    public void setProgressiveBlur(BlurTarget target, int direction, boolean applyNoise,
                                   int legacyBgStartColor, int legacyBgEndColor) {
        if (!isLegacy) {
            blurView.setupWith(target, BlurController.DEFAULT_SCALE_FACTOR, applyNoise)
                    .setBlurGradient(direction)
                    .setBlurRadius(blurRadius);

            blurView.setOutlineProvider(viewOutlineProvider);
            blurView.setClipToOutline(true);
        } else {
            Drawable bg = new ViewBackgroundJavaBuilder()
                    .setGradient(legacyBgStartColor, legacyBgEndColor, getAngle(direction))
                    .setCornerRadius(cornerRadiusDp)
                    .build(blurView.getContext());
            if (bg != null) {
                blurView.setBackground(bg);
            }
        }
    }

    private int getAngle(int direction) {
        switch (direction) {
            case BlurView.GRADIENT_TOP_TO_BOTTOM: return 270;
            case BlurView.GRADIENT_BOTTOM_TO_TOP: return 90;
            case BlurView.GRADIENT_LEFT_TO_RIGHT: return 0;
            case BlurView.GRADIENT_RIGHT_TO_LEFT: return 180;
            default: return 0;
        }
    }
}
