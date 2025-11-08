<div align="center">

## AndroidLiquidGlassView
**Android 仿 iOS 26 液态玻璃效果，AndroidLiquidGlassView 库具有真实的折射和色散效果**

<br>
<br>

  <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="Apache"/>
  <img src="https://img.shields.io/badge/Android-13.0%2B-brightgreen.svg" alt="Android 5"/>
  <img src="https://img.shields.io/badge/targetSdk-36-green" alt="targetSdk"/>
  <img src="https://img.shields.io/maven-central/v/com.qmdeve/AndroidLiquidGlassView" alt="maven"/>
  <img src="https://img.shields.io/github/stars/QmDeve/AndroidLiquidGlassView" alt="Stars"/>

<br>
<br>

[English](https://github.com/QmDeve/AndroidLiquidGlassView/blob/master/README.md) | 简体中文

<br>

[QQ 交流群](https://qm.qq.com/q/46EanJ9nN6)

</div>

---

## 特性
 - **`液态玻璃`效果 - 真实的折射与色散效果**
 - **高度可定制 - 支持调整圆角半径、折射高度、折射偏移、色散参数、模糊半径、色调等**

---

## 要求
 - **Android API 33 +（Android 13），以获得完整的玻璃效果**

---

## 截图

<img src="https://github.com/QmDeve/AndroidLiquidGlassView/blob/master/img/image.png?raw=true" alt="Stars"/>

---

## Demo 演示
[下载 Demo](https://github.com/QmDeve/AndroidLiquidGlassView/blob/master/app/release/app-release.apk)

---

## 快速集成
[![Maven Central](https://img.shields.io/maven-central/v/com.qmdeve/AndroidLiquidGlassView)](https://central.sonatype.com/artifact/com.qmdeve/AndroidLiquidGlassView)
### 添加依赖项：
**将以下依赖项添加到模块的`build.gradle`中:**
```gradle
dependencies {
   implementation 'com.qmdeve:AndroidLiquidGlassView:1.0.0-alpha08'
}
```

---

## 如何使用
### XML 布局
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/root"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- 内容容器 -->
    <FrameLayout
        android:id="@+id/content_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">

            <ImageView
                android:id="@+id/images"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:src="@drawable/image" />

        </RelativeLayout>
    </FrameLayout>

    <!-- 液态玻璃视图 -->
    <com.qmdeve.liquidglass.widget.LiquidGlassView
        android:id="@+id/liquidGlassView"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_centerInParent="true" />
</RelativeLayout>
```

---

## 绑定采样源
```java
ViewGroup content = findViewById(R.id.content_container);
LiquidGlassView liquidGlassView = findViewById(R.id.liquidGlassView);

liquidGlassView.bind(content);
```

## API 参考
| 方法                                | 描述                           | 默认值    |
|-----------------------------------|------------------------------|--------|
| `bind(ViewGroup source)`          | **绑定采样源**                    | `-`    |
| `setCornerRadius(float px)`       | **设置圆角半径 (px) (0dp-99dp)**   | `40dp` |
| `setRefractionHeight(float px)`   | **设置折射高度 (px) (12dp-50dp)**  | `20dp` |
| `setRefractionOffset(float px)`   | **设置折射偏移 (px) (20dp-120dp)** | `70dp` |
| `setTintColorRed(float red)`      | **设置色调（R） (0f-1f)**          | `1.0f` |
| `setTintColorGreen(float green)`  | **设置色调（G） (0f-1f)**          | `1.0f` |
| `setTintColorBlue(float blue)`    | **设置色调（G） (0f-1f)**          | `1.0f` |
| `setTintAlpha(float alpha)`       | **设置色调可见度 (0f-1f)**          | `0.0f` |
| `setDispersion(float dispersion)` | **设置色散效果 (0f-1f)**           | `0.5f` |
| `setBlurRadius(float radius)`     | **设置模糊半径 (0dp-50dp)**        | `0f`   |
| `setDraggable(boolean enable)`    | **启用/禁用拖动**                  | `true` |

---

## 效果说明
**在`Android 13`及更高版本的设备上，将渲染完整的`液化玻璃`效果，包括：**
 - **`真实的折射效果`**
 - **`色散效果`**
 - **`可调节的模糊效果`**
 - **`可调节的色调效果`**

**在`Android 13`以下的设备上，View将保持透明的背景，不会呈现任何效果**

## 注意事项
**1.`采样源`：** 确保绑定采样源视图包含有效内容

**2.`兼容性`：** 仅在`Android 13+`上渲染完整效果

---

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=QmDeve/AndroidLiquidGlassView&type=date&legend=bottom-right)](https://www.star-history.com/#QmDeve/AndroidLiquidGlassView&type=date&legend=bottom-right)

---

### 我的其他开源库
- **[QmBlurView](https://github.com/QmDeve/QmBlurView)**
 - **[QmReflection](https://github.com/QmDeve/QmReflection)**

---

### 赞助我们

**如果您觉得我们的项目对您有帮助，欢迎通过以下方式赞助支持：**

![赞助二维码](https://youke1.picui.cn/s1/2025/11/04/6909d2ae165f0.png)