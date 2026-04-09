# BlurView

[中文版 README_CN.md](README_CN.md)

Cloned from [Dimezis/BlurView](https://github.com/Dimezis/BlurView) with added support for **Gradient/Progressive Blur** and a new utility class **`BlurView3Util`** for easier setup and backward compatibility.

## What's New

- **Gradient/Progressive Blur**: Supports directional blur fading (e.g., Top to Bottom, Left to Right) to smoothly transition between blurred and sharp content.
- **`BlurView3Util`**: A utility class to easily apply standard or progressive blur with an automatic fallback (solid/gradient background) for older Android versions (API < 31 / Android 12).

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

### 2. Setup with BlurView3Util

Initialize `BlurView3Util` and apply the desired blur effect. The utility automatically handles fallback backgrounds for devices running API < 31 (Android 12).

#### Standard Blur

```java
BlurTarget blurTarget = findViewById(R.id.blurTarget);
BlurView blurView = findViewById(R.id.blurView);

// Initialize util with BlurView, corner radius (in dp), and blur radius
BlurView3Util blurUtil = new BlurView3Util(blurView, 16, 20f);

// Apply standard blur with a fallback color for legacy devices
blurUtil.setBlur(blurTarget, Color.parseColor("#80000000"));
```

#### Progressive / Gradient Blur

```java
BlurTarget blurTarget = findViewById(R.id.blurTarget);
BlurView blurView2 = findViewById(R.id.blurView2);

BlurView3Util progressiveBlurUtil = new BlurView3Util(blurView2, 0, 20f);

// Apply progressive blur with a fallback gradient for legacy devices
progressiveBlurUtil.setProgressiveBlur(
    blurTarget, 
    BlurView.GRADIENT_TOP_TO_BOTTOM, // Blur direction
    false,                           // applyNoise (recommended false to avoid visible boundaries)
    Color.parseColor("#80000000"),   // Legacy start color
    Color.TRANSPARENT                // Legacy end color
);
```