package eightbitlab.com.blurview;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

/**
 * Blur background
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
     * @param cornerRadius BlurView corner radius. Note: no dp conversion needed, handled internally.
     * @param blurRadius Blur radius
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
     * Set standard blur
     *
     * @param target Blur target
     * @param legacyBgColor Legacy fallback color
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
     * Set progressive blur
     *
     * @param target Blur target
     * @param direction Blur direction
     * @param applyNoise Whether to apply noise, false is recommended, otherwise there will be a visible boundary
     * @param legacyBgStartColor Legacy fallback start color
     * @param legacyBgEndColor Legacy fallback end color
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
