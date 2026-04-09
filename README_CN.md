# BlurView

[English README](README.md)

克隆自 [Dimezis/BlurView](https://github.com/Dimezis/BlurView)，新增了对 **渐变模糊 (Gradient/Progressive Blur)** 的支持，并提供了一个实用的 **`BlurView3Util`** 工具类，以简化配置和向下兼容处理。

## 新增特性

- **渐变模糊 (Gradient/Progressive Blur)**：支持有方向的模糊渐变（例如从上到下、从左到右），使模糊部分和清晰部分之间的过渡更加自然。
- **`BlurView3Util` 工具类**：用于快速设置常规模糊或渐变模糊，并自动为旧版 Android（API < 31 / Android 12）提供后备的纯色或渐变色背景处理。

## 使用方法

### 1. XML 布局设置

首先，将你的背景内容包裹在一个 `BlurTarget` 容器内，然后将 `BlurView` 放置在它的上方。

```xml
<eightbitlab.com.blurview.BlurTarget 
    android:id="@+id/blurTarget" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent"> 
    
    <!-- 你的底层内容放置在这里 -->
    <com.allan.androidlearning.picwall.InfiniteCanvasView 
        android:id="@+id/infiniteCanvasView" 
        android:layout_width="match_parent" 
        android:layout_height="match_parent" /> 
</eightbitlab.com.blurview.BlurTarget> 

<!-- 覆盖在目标内容上方的 BlurView -->
<eightbitlab.com.blurview.BlurView 
    android:id="@+id/blurView" 
    app:layout_constraintTop_toTopOf="parent" 
    android:layout_width="match_parent" 
    android:layout_height="120dp" /> 

<eightbitlab.com.blurview.BlurView 
    android:id="@+id/blurView2" 
    app:layout_constraintBottom_toBottomOf="parent" 
    android:layout_width="match_parent" 
    android:layout_height="120dp" /> 
```

### 2. 使用 BlurView3Util 配置

初始化 `BlurView3Util` 工具类并应用所需的模糊效果。该工具会自动为 API < 31 (Android 12) 的设备提供降级背景支持。

#### 常规模糊

```java
BlurTarget blurTarget = findViewById(R.id.blurTarget);
BlurView blurView = findViewById(R.id.blurView);

// 初始化工具类，传入 BlurView、圆角大小(dp) 和 模糊半径
BlurView3Util blurUtil = new BlurView3Util(blurView, 16, 20f);

// 应用常规模糊，并为旧设备提供一个降级的背景色
blurUtil.setBlur(blurTarget, Color.parseColor("#80000000"));
```

#### 渐变模糊 / 渐进模糊

```java
BlurTarget blurTarget = findViewById(R.id.blurTarget);
BlurView blurView2 = findViewById(R.id.blurView2);

BlurView3Util progressiveBlurUtil = new BlurView3Util(blurView2, 0, 20f);

// 应用渐变模糊，并为旧设备提供降级的渐变背景色
progressiveBlurUtil.setProgressiveBlur(
    blurTarget, 
    BlurView.GRADIENT_TOP_TO_BOTTOM, // 模糊渐变方向
    false,                           // applyNoise (建议传 false 以避免明显的噪点边界)
    Color.parseColor("#80000000"),   // 旧设备的降级起始颜色
    Color.TRANSPARENT                // 旧设备的降级结束颜色
);
```