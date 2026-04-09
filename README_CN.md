# BlurView

[English README](README.md)

克隆自 [Dimezis/BlurView](https://github.com/Dimezis/BlurView)，新增了对 **渐变模糊 (Gradient/Progressive Blur)** 的支持，并提供了一个实用的 **`BlurView3Util`** 工具类，以简化配置和向下兼容处理。

## 引入依赖

在你的 module 级 `build.gradle` 中添加以下依赖：

```gradle
dependencies {
    implementation 'io.github.jzlhll:blurview:3.2.1'
}
```

## 新增特性

- **渐变模糊 (Gradient/Progressive Blur)**：支持有方向的模糊渐变（例如从上到下、从左到右），使模糊部分和清晰部分之间的过渡更加自然。
- **`BlurView3Util` 工具类**：用于快速设置常规模糊或渐变模糊，并自动为旧版 Android（API < 31 / Android 12）提供后备的纯色或渐变色背景处理。
- **叠加色 (Overlay) 支持**：支持在常规模糊或渐变模糊上叠加一层颜色。

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

### 2. 使用 BlurView3Util 配置与效果测试

初始化 `BlurView3Util` 工具类并应用所需的模糊效果。下面列出了四种常见的设置方式及其测试效果评估。该工具会自动为 API < 31 (Android 12) 的设备提供降级背景支持。

#### A. 常规模糊

```kotlin
// 测试了：blurView，圆角，模糊半径，过时的替代颜色
BlurView3Util(binding.blurView, 16, 3f).setBlur(
    binding.blurTarget, 
    Color.parseColor("#ccffffff")
)
```

#### B. 带有叠加色的常规模糊 (推荐)

```kotlin
// 测试了：blurView，圆角，模糊半径，追加overlayColor
// 结果：效果还不错，值得推荐
BlurView3Util(binding.blurView, 16, 3f).setBlurWithOverlay(
    binding.blurTarget, 
    Color.parseColor("#20000000"), // overlayColor
    Color.parseColor("#ccffffff")  // legacyBgColor
)
```

#### C. 渐变模糊 / 渐进模糊

```kotlin
// 测试了：渐变模糊效果
// 结果：applyNoise 推荐传 false，否则可能会有明显的分界线
BlurView3Util(binding.blurView, 0, 16f).setProgressiveBlur(
    binding.blurTarget, 
    BlurView.GRADIENT_TOP_TO_BOTTOM, 
    false, // applyNoise (建议传 false)
    Color.parseColor("#ccffffff"), // 旧设备的降级起始颜色
    Color.parseColor("#00ffffff")  // 旧设备的降级结束颜色
)
```

#### D. 带有叠加色的渐变模糊 (不推荐)

```kotlin
// 测试了：渐变模糊，追加overlayColor
// 结果：视觉效果较差，不推荐在渐变模糊上追加 overlayColor
BlurView3Util(binding.blurView, 0, 16f).setProgressiveBlurWithOverlay(
    binding.blurTarget, 
    BlurView.GRADIENT_TOP_TO_BOTTOM, 
    false, 
    Color.parseColor("#20000000"), // overlayColor
    Color.parseColor("#ccffffff"), 
    Color.parseColor("#00ffffff")
)
```