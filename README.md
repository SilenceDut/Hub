## Hub [![](https://jitpack.io/v/SilenceDut/Hub.svg)](https://jitpack.io/#SilenceDut/Hub)
a concise di library which can avoid check null when want to invoke a implementation by interface

## Project

It had been used in project [KnowWeather](https://github.com/SilenceDut/KnowWeather) ,you can learn more.

同时已用在日活百万级别的线上项目中，目前没出现任何问题

## Feature

1. 通过控制反转实现module间服务提供、Activity跳转，Activity支持参数自动处理传递的参数，不需要繁琐的注解标注
2. 多接口支持。优点在于不必要暴露所有接口,只需将需要的接口暴露，比如一个服务可能支持多个功能，但是有些功能只需要再module内使用，有些需要提供给其他module，这样就可以抽离出多个接口，只需要将需要暴露的放到基础module里。
3. 支持**多进程Activity跳转**的参数自动处理
4. 接口化的通信方式类似于“SDK”+数据结构，面向接口编程，更清晰直观， 对IDE更友好（可在IDE中直接跳转），协议变化直接反映在编译上，维护接口也简单
5. 不需要繁琐的判空处理，服务的实现类或者路径的Activity不存在时不会崩溃。Activity跳转失败可以通过方法返回值来感知，做一些异常处理
6. **Less is more, simple is better!使用简单，功能强大**

## Using

### 简单的使用方式

坚持 **Less is more, simple is better!** 的理念，接口定义、调用的方式都很是常规的方式，尽可能的减少注解来标注，使配置最小化。

#### 服务提供


向其他module提供的接口(在common module里定义)
```java
public interface IFunctionOuter extends IHub{
	void testOut();
}
```

不需要暴露的接口（A module里定义）

```java
public interface IFunctionInner extends IHub{
	void testInner();
}
```

实现功能（ A module里实现）
```java
@HubInject(api = {IFunctionInner.class, IFunctionOuter})
class FunctionImpl implements IFunctionInner , IFunctionOuter{
    @Override
    public void testInner(){
    ...
    }
    
    @Override
    public void testOut(){
    ...
    }
}
```

不需要判空、强转，直接调用（任何module里都可以调用）

```java
Hub.getImpl(IFunctionOuter.class). testOut();
```

#### Activity跳转

在Application里初始化，这里只是传入Application引用做缓存，主要是为了后面跳转Activity时减少传入Context的过程，只是一个赋值操作，没有做任何耗时的操作，如果不需要**Activity跳转**，只需要**服务提供**,这个初始化就不要添加

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Hub.init(this);
    }
}
```

如果需要判断是否存在相应的页面来做一些提示之类的，可以将函数返回值的类型设置为boolean

```java
public interface IActivityTest extends IHubActivity{
    boolean anyMethodName(List<Map<String,Integer>> a, int b); 
    void activity2(); // no need use at present
}
```

在需要调转的Activity上加上HubActivity注解，通过methodName也指定当前Activity对应的接口的方法，如果需要传参数，参数名和类型和接口里的保持一致，不需要注解，跳转完成后，Activity的参数会自动被解析（支持跨进程的Activity）

```java
@HubActivity(activityApi = IActivityTest.class,methodName = "anyMethodName")
public class SecondActivity extends AppCompatActivity {
    List<Map<String,Integer>> a; // 参数名和类型保持一致
    int b; // 参数名和类型保持一致
    ...
}
```
调用
```java
boolean found = Hub.getActivity(IActivityTest.class).activitySecond(mapList,9);

如果需要activityResult 

Hub.getActivityWithExpand(IActivityTest.class).withResult(MainActivity.this,10).build().activitySecond(mapList,9);
```


## Import

**Step1.Add it in your root add abuild.gradle at the end of repositories:**

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```


**Step2. Add the dependency:**

```java
dependencies {
    implementation 'com.github.SilenceDut.Hub:hub:latestVersion'
    annotationProcessor 'com.github.SilenceDut.Hub:hub-compiler:latestVersion'
}
```

**ProGuard**
```java
-keep class * implements com.silencedut.hub_annotation.IFindImplClz {*;}
-keep class * implements com.silencedut.hub_annotation.IFindActivity {*;}
-keepnames interface * extends com.silencedut.hub.IHub
-keepnames interface * extends com.silencedut.hub.IHubActivity
```

## License
```
Copyright 2017-2018 SilenceDut

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