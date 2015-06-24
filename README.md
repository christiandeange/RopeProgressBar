# RopeProgressBar

Android ProgressBar that "bends" under its own weight. Inspired by http://drbl.in/nwih

![RopeProgressBar Animation](ropeprogressbar.gif)

---
###Attributes

| Attribute            | Type      | Usage                                                        |
| -------------------- | --------- | ------------------------------------------------------------ |
| `app:max`            | integer   | The max value of the progress bar                            |
| `app:progress`       | integer   | The current value of the progress bar                        |
| `app:primaryColor`   | color     | Color used for the progress completed                        |
| `app:secondaryColor` | color     | Color used for the progress remaining                        |
| `app:slack`          | dimension | The max vertical "bend" of the progress bar                  |
| `app:strokeWidth`    | dimension | The width of the progress bar line                           |
| `app:dynamicLayout`  | boolean   | If the progress bar should change in height as slack changes |

---
###Download

```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    compile 'com.github.cdeange:RopeProgressBar:0.1.0'
}
```

---
###Developed By
- Christian De Angelis - <de@ngelis.com>

---
###License

```
Copyright 2015 Christian De Angelis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
