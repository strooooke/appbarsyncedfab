# appbarsyncedfab
An Android library for getting a FAB to slide in and out in sync with a scrolling AppBarLayout.

## Appearance

![SimpleAppBar](https://cloud.githubusercontent.com/assets/856723/15891088/370d3210-2d73-11e6-866a-69ec9b1fcde1.gif) ![ComplexAppBar](https://cloud.githubusercontent.com/assets/856723/15934370/98bdee78-2e63-11e6-88b9-8669de9ee57a.gif)

## Usage

Add as gradle dependency via [jitpack.io]: Add the JitPack repository in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```

Add the dependency in your app build.gradle file:
```
	dependencies {
	        implementation 'com.github.strooooke:appbarsyncedfab:v0.5'
	}
```

Add either the behavior to your FAB in XML
```
<androidx.coordinatorlayout.widget.CoordinatorLayout
    ...
  >
  
  <com.google.android.material.appbar.AppBarLayout
    ...
    >
  </com.google.android.material.appbar.AppBarLayoutt>

  ...

  <com.google.android.material.floatingactionbutton.FloatingActionButton
    ...
    app:layout_behavior="@string/appbarsyncedfab_fab_behavior"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

You can also wire up the listener, the CoordinatorLayout, the AppBarLayout and the FAB by hand:
```
CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
AppBarLayout appBarLayout = findViewById(R.id.app_bar);
FloatingActionButton fab = findViewById(R.id.fab); 
FabOffsetter fabOffsetter = new FabOffsetter(coordinatorLayout, fab);
appBarLayout.addOnOffsetChangedListener(fabOffsetter);
```
This way is not recommended; you will lose proper interaction with the snackbar.

## License

    Copyright 2016-2019 Juliane Lehmann <jl@lambdasoup.com>
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[jitpack.io]: https://jitpack.io/#strooooke/appbarsyncedfab
