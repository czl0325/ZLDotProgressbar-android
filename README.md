# ZLDotProgressbar-android
安卓版时间节点的控件

 
![](https://github.com/czl0325/ZLDotProgressbar-android/blob/master/screen.gif)

 
使用方法：
 
xml 上：
 
```
<com.czl.zldotprogressbar2.ZLDotProgressBar
        android:id="@+id/progressbar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        app:barDotsCount="5"
        app:barDotsRadius="15dp"
        app:barProgressWidth="10dp"
        app:barFrontColor="@color/colorPrimary"
        app:barBackColor="#aaa"
        />
```
 
代码：
```
    progressBar = (ZLDotProgressBar)findViewById(R.id.progressbar);
    progressBar.setmTexts(Arrays.asList(new String[]{"已申请", "初审中", "预授结果", "复审中", "结束"}));
    progressBar.setNewProgress(3);
```
