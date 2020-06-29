## 网络创新实验 期末报告

姓名：罗旭川           &emsp;               学号：17307130162              &emsp;              时间：2020.3 ~ 2020.6

#### 一、项目名称

​		Student   Elearning  App

#### 二、项目介绍

##### 1. 项目功能、用途与设计思想

​       本项目为学校elearning系统的手机App版，使掌上浏览elearning成为了可能。在实现elearning大部分原有功能的基础上，本项目还针对elearning现有的缺陷做出有针对性地改进，实现对elearning功能的筛选、发扬和再创造。

​		在用途方面，就是让学生们能十分便捷地浏览elearning上的课件、作业等信息，另一方面，还可以通过日历功能进行待办的记录等，具体的功能与效果将在后面叙述。

​       本项目的设计思想为用户至上。即尽可能地提升用户的使用体验，在界面上做到尽可能的美观，在功能上做到尽可能的人性化。

##### 2. 总体设计与框图

![](.\Report\框图.png)

​        框图中客户端为用户的手机客户端，服务端为服务器端。

​		在客户端，Login模块负责用户的登陆界面，它包含的信息管理模块用于在用户手机上储存用户的用户名、密码；主模块即用户登录成功后进入的主界面，它包含Dashboard、Calendar、Home三个功能区，分别用于用户的课程管理、代办管理、个人信息管理。

​        其中，Dashboard功能区由若干个Course模块组成，每个Course模块代表用户所选的一门课程。在每个Course模块中，用户可以分别浏览公告、作业、人员、文件信息，这些分别由相应的模块实现，还可以通过Color模块为课程卡片换色。Home功能区除了复用的Files模块和Color模块外，还包含一个All-to-do模块，主要是用于对用户所有待办事项的集中显示。Calendar功能区由于功能之间联系紧密，因此没有子模块。

​		在服务端，按处理方式主要分两类请求。若是对TOKEN的请求，则利用爬虫的手段，通过用户的用户名和密码获取用户TOKEN，然后将其返回给客户端。在这之后的所有客户端请求都将附带TOKEN。对于这些其它请求，服务端将根据请求的内容构造相应的新请求，然后将之发送给Elearning-API文档所提供的相应接口获取用户数据，然后将数据清理、整理后发送回客户端。



##### 3. 功能的效果展示

##### 1） 登录

<img src=".\Report\1.1.PNG" style="zoom:1%;" />     <img src=".\Report\1.2.PNG" style="zoom:1%;" />

若之前已登录过，则用户名和密码将会被自动填充；每当进行数据请求时界面都会有loading动画。

##### 2）Dashboard功能区

<img src=".\Report\2.1.PNG" style="zoom:5%;" />     <img src=".\Report\2.2.PNG" style="zoom:5%;" />     <img src=".\Report\2.3.PNG" style="zoom:5%;" />

* 查看公告（Announcements）

  <img src=".\Report\2.4.PNG" style="zoom:5%;" />     <img src=".\Report\2.5.PNG" style="zoom:5%;" />     <img src=".\Report\2.6.PNG" style="zoom:4%;" />

  可以在公告列表中查看该课程的公告，长公告支持滚动，同时支持图片渲染。这里是通过HTML模块实现的。

* 查看文件（Files）

  <img src=".\Report\2.7.PNG" style="zoom:5%;" />     <img src=".\Report\2.8.PNG" style="zoom:5%;" />     <img src=".\Report\2.9.PNG" style="zoom:5%;" /> 

  如果是文件夹则可以继续打开，如果是文件则可以点击进行在线浏览，各类型文件用图标区分。在线浏览的功能在模拟器中无法运行，在真机上则可以，如下图：

  <img src=".\Report\2.10.PNG" style="zoom:5%;" />     <img src=".\Report\2.11.PNG" style="zoom:5%;" />    <img src=".\Report\2.12.PNG" style="zoom:5%;" /> 

  在线浏览时可以放大、滚动。

* 查看作业（Homework）

  <img src=".\Report\2.13.PNG" style="zoom:5%;" />     <img src=".\Report\2.14.PNG" style="zoom:5%;" /> 

  可以在作业列表中查看该课程的作业，列表项中绿色图标表示已完成，未完成将是红色。点击进入可以查看该项作业的详细要求，这里也是通过HTML模块渲染实现的。

* 查看人员（Members）

  <img src=".\Report\2.15.PNG" style="zoom:4%;" /> 

  在人员列表中可以查看该课程的人员信息，包括老师、助教和学生的头像、姓名、学号信息。

* 修改课程卡片颜色

  <img src=".\Report\2.16.PNG" style="zoom:5%;" />     <img src=".\Report\2.17.PNG" style="zoom:5%;" />     <img src=".\Report\2.18.PNG" style="zoom:5%;" /> 

  点击右上角的颜色板按键，可以为该课程换色，除了选择颜色，还可以调节亮度。如图换成了深粉色。

##### 3）Calendar功能区

<img src=".\Report\3.1.PNG" style="zoom:5%;" />     <img src=".\Report\3.2.PNG" style="zoom:5%;" />     <img src=".\Report\3.3.PNG" style="zoom:5%;" />

上下滑动切换周视图和月视图；左右滑动则是进入上一周/月和进入下一周/月。

* 一句话任务的新增、删除、查询

  <img src=".\Report\3.5.PNG" style="zoom:5%;" />     <img src=".\Report\3.6.PNG" style="zoom:5%;" />     <img src=".\Report\3.8.PNG" style="zoom:5%;" />

  * 日历右上角3个按键分别为：跳到今日，新增任务，切换周月视图

  * 每个任务项右边的2个按键分别为：设为已完成，删除任务

* 每日任务的完成情况用颜色标记

  <img src=".\Report\3.9.PNG" style="zoom:5%;" /> 

  * 绿色：表示过去某一天的任务全部完成
  * 红色：表示过去某一天还有任务没有完成
  * 蓝色：表示将来某一天有计划

##### 4）Home功能区

<img src=".\Report\4.1.PNG" style="zoom:4%;" />     <img src=".\Report\4.7.PNG" style="zoom:4%;" />

* 个人界面的右上角3个按键分别为：退出登录，清空文件缓存，修改个人颜色

  <img src=".\Report\4.2.PNG" style="zoom:4%;" />     <img src=".\Report\4.3.PNG" style="zoom:5%;" />     <img src=".\Report\4.4.PNG" style="zoom:4%;" />   

* 个人代办事项的集中显示

  <img src=".\Report\4.5.PNG" style="zoom:5%;" /> 

  包括未完成的作业，以及在日历中进行的计划，都会在这里集中显示。

* 个人文件夹

  <img src=".\Report\4.6.PNG" style="zoom:5%;" />     <img src=".\Report\4.8.PNG" style="zoom:5%;" /> 

  这里复用的和课程文件一样的Files模块，可以在这里找到自己提交的课程作业文件。



##### 4. 项目部署方法

部署前的说明：

a. 保持网络连接，若是模拟器要注意设置好网络，确保网络能连接到服务端（特别是对于android自带的模拟器，可以使用 `emulator @[模拟器设备名] -dns-server 8.8.8.8,114.114.114.114` 指令来使用Google提供的DNS地址启动模拟器）

b. 第一次登陆会有点慢，因为需要爬虫获取TOKEN，请耐心等待



##### 1）方法一：使用远端服务器

这种方法将使用我已经在阿里云服务器上搭建的服务端。（2020.10到期）

如果认为不够安全，则可以在自己的服务器上将后端代码进行部署，然后修改源码中的 `com.example.elearning.ElearningApi` 中开头的 `MyBackend` 字符串并重新build出apk来安装运行。

* 将 `elearning.apk` 直接安装在真机或模拟器上即可登录运行。

##### 2）方法二：使用本地服务端

* 使用python3运行 `ElearningBackend\MyBackend.py` 来在本机启动服务端；

* 使用 `ipconfig` 找到本机的 IP 地址；

* 检查网络：在要运行 app 的设备（真机或模拟器）上访问 `http://[本机IP地址]:5555/hello`，若能访问成功，说明本地服务端可用，则可以直接进行下一步；若访问失败，请检查真机或模拟器的网络设置；

* 修改源码中的 `com.example.elearning.ElearningApi` 中开头的 `MyBackend` 字符串为 `http://[本机IP地址]:5555`，它表示服务端的 url；

* 重新 run 源代码即可启动 app 并登录运行（这里要注意 gradle 版本等问题）。

  

#### 三、项目难点与解决技术

##### 1. 登录比较麻烦

* 难点描述：学校elearning的所有api都需要用TOKEN作为请求的凭证，而获取TOKEN的api又需要用学校elearning系统的 `Develop Key` 作为请求的凭证。即没有权限的问题。
* 解决方法：
  * 通过爬虫手段获取TOKEN信息。本项目直接使用的是 `Selenium` 自动化工具来爬取用户的TOKEN。这种方法优点是简单直接，缺点是有点慢。
  * 解决爬取慢的问题。我在手机客户端实现了信息管理模块，不仅用来存储用户名和密码，还将用户第一次登录所获取的TOKEN也存储在了手机客户端中（具体为存储在 `SharedPreferences` 中），由于爬虫获取的是无限期的TOKEN，因此理论上一部手机安装完这个app后就只需要爬虫一次，之后的请求就直接使用本地已经存储的TOKEN即可。
* 技术总结：`Selenium`、`android.content.SharedPreferences`

##### 2. 在线浏览如何实现

* 难点描述：android原生的web浏览器是没有办法打开复杂的文档的，比如elearning中常见的ppt、pdf类型的文件。
* 解决方法：
  * 使用第三方的文件在线浏览技术，这里选择的是 `腾讯TBS服务`。腾讯TBS服务简单来说就是使用一种基于`x5` 内核的web浏览器来实现文件的在线浏览，目前可以支持多达46种文件的打开，像在微信、QQ等中打开文件都是使用的这种技术。缺点是由于模拟器中不支持x5内核，因此在模拟器中无法使用该功能。
  * 解决模拟器无法使用的问题。即进行真机调试。由于我没有安卓手机，因此这里需要请有安卓手机的同学帮忙远程调试。

* 技术总结：`腾讯TBS服务`。接入教程参考官网 https://x5.tencent.com/tbs/guide/sdkInit.html

##### 3. 需要实现的模块太多了

* 难点描述：由于app的功能繁多，项目工程量实在巨大。具体见前面的设计框图。

* 解决方法：

  * 尽可能复用模块。在设计时，我将大部分界面设计为相似的列表结构，比如Course模块中的公告列表、文件列表、作业列表、人员列表以及Home模块中的待办列表，最终复用了同一个 `ListView`，只是每种列表项的布局和点击效果不同罢了。而要实现一个能装入不同类型Item的ListView，就需要设计一个可复用的自定义 `BaseAdapter`， Adapter是一种用来帮助填充数据的中间桥梁。 
  * 多使用第三方库。
    * 为简化网络请求代码的复杂性，使用了开源的网络框架`okhttp3`
    * 为简化json数据解析的复杂性，使用了第三方库`fastjson`
    * 为简化图片的加载和缓冲的复杂性，使用了第三方库 `glide`

* 技术总结：

  1） `可复用的自定义BaseAdapter`。参考教程：https://www.runoob.com/w3cnote/android-tutorial-customer-baseadapter.html。最终的实现位于 `com.example.elearning.util.SuperAdapter`

  2）好用的网络请求框架 `okhttp3`。导入包：`com.squareup.okhttp3:okhttp:4.4.0`

  3）好用的json解析第三方库 `fastjson`。导入包：`com.alibaba:fastjson:1.1.55.android`

  4）好用的图片处理第三方库 `glide`。导入包：`com.github.bumptech.glide:glide:3.7.0`

##### 4. 界面美化太难了

* 难点描述：安卓原生开发的界面美化实在困难，很难达到像网页那样的界面质量。

* 解决方法：

  * 尽可能挖掘可以美化的地方。例如我在课程卡片上增加了阴影效果，点击时增加了涟漪效果等，并且还重新自定义了toast的样式，这些是原生android可以实现的技术。
  * 排版时设计尽可能规整。排版时大部分使用的是 `LinearLayout` 和 `RelativeLayout`，也因此支持横屏。
  * 多使用第三方库。如 `Loading` 界面、`Calendar` 界面、`Color` 选择器，都使用了相应第三方库。

* 技术总结：

  1）`涟漪效果`。最终实现的类位于 `com.example.elearning.util.MyButton`

  2）好看的 `Loading` 界面。导入包：`com.zyao89:zloading:1.2.0`

  3）好看的 `Calendar` 界面。导入包：`com.haibin:calendarview:3.6.6`

  4）好看的 `Color` 选择器。导入包：`com.github.QuadFlask:colorpicker:0.0.15`



#### 四、项目总结

* 选择一个有趣的项目内容很重要。若不是觉得有趣，我一定是做不完这个项目的。
* 多利用第三方库很重要。若不是借用了许多好用或好看的第三方库，我一定是做不到这样的效果的。并且学习的这些第三方库若以后还做app的话一定还能用上的。
* 一些遗憾。可惜这个app只能用在安卓手机上，并且我也不是用的安卓手机...不过感觉如果能给周围的同学带来便利，就已经很满足了！
