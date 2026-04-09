# BlurView

[中文版 README_CN.md](README_CN.md)

Cloned from [Dimezis/BlurView](https://github.com/Dimezis/BlurView) with added support for **Gradient/Progressive Blur** and a new utility class **`BlurView3Util`** for easier setup and backward compatibility.

## Dependency

Add this to your module's `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.jzlhll:blurview:3.2.1'
}
```

## What's New

- **Gradient/Progressive Blur**: Supports directional blur fading (e.g., Top to Bottom, Left to Right) to smoothly transition between blurred and sharp content.
- **`BlurView3Util`**: A utility class to easily apply standard or progressive blur with an automatic fallback (solid/gradient background) for older Android versions (API < 31 / Android 12).
- **Overlay Support**: Methods are provided to add an overlay color over standard or progressive blurs.

## Usage

### 1. XML Layout Setup

First, wrap your background content inside a `BlurTarget`. Then, place your `BlurView` over it.

```xml
<eightbitlab.com.blurview.BlurTarget 
    android:id="@+id/blurTarget" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent"> 
    
    <!-- Your underlying content goes here -->
    <com.allan.androidlearning.picwall.InfiniteCanvasView 
        android:id="@+id/infiniteCanvasView" 
        android:layout_width="match_parent" 
        android:layout_height="match_parent" /> 
</eightbitlab.com.blurview.BlurTarget> 

<!-- Blur views positioned over the target -->
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

### 2. Setup with BlurView3Util & Effects Comparison

Initialize `BlurView3Util` and apply the desired blur effect. Below are four common setups along with test conclusions. The utility automatically handles fallback backgrounds for devices running API < 31 (Android 12).

#### A. Standard Blur

```kotlin
// Tested: blurView, corner radius, blur radius, legacy fallback color
BlurView3Util(binding.blurView, 16, 3f).setBlur(
    binding.blurTarget, 
    Color.parseColor("#ccffffff")
)
```

#### B. Standard Blur with Overlay (Recommended)

```kotlin
// Tested: blurView, corner radius, blur radius, overlayColor
// Result: Looks great! Highly recommended for adding a subtle tint to the blurred area.
BlurView3Util(binding.blurView, 16, 3f).setBlurWithOverlay(
    binding.blurTarget, 
    Color.parseColor("#20000000"), // overlayColor
    Color.parseColor("#ccffffff")  // legacyBgColor
)
```

#### C. Progressive / Gradient Blur

```kotlin
// Tested: Progressive blur fading
// Result: `applyNoise = false` is recommended. If true, it might show a visible boundary.
BlurView3Util(binding.blurView, 0, 16f).setProgressiveBlur(
    binding.blurTarget, 
    BlurView.GRADIENT_TOP_TO_BOTTOM, 
    false, // applyNoise (recommended false)
    Color.parseColor("#ccffffff"), // Legacy start color
    Color.parseColor("#00ffffff")  // Legacy end color
)
```

#### D. Progressive Blur with Overlay (Not Recommended)

```kotlin
// Tested: Progressive blur + overlayColor
// Result: Visual effect is relatively poor. Adding an overlay to progressive blur is NOT recommended.
BlurView3Util(binding.blurView, 0, 16f).setProgressiveBlurWithOverlay(
    binding.blurTarget, 
    BlurView.GRADIENT_TOP_TO_BOTTOM, 
    false, 
    Color.parseColor("#20000000"), // overlayColor
    Color.parseColor("#ccffffff"), 
    Color.parseColor("#00ffffff")
)
```