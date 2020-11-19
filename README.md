## 百度语音识别/语音合成服务本地化部署

## 基于Android Studio开发集成##

### step1: 项目根目录build.gradle添加配置###

```bash
allprojects {
   repositories {
       ...yours...
       maven { url 'https://jitpack.io' } //使用GitHub仓库
   }
}
```


### step2： app根目录build.gradle添加依赖###

```bash
dependencies {
   ...yours...
   implementation 'com.github.hezh-0617:MjxxSDK:native_1.0.3' //添加依赖
}
```


### step3：目标入口处添加代码###
```bash
Config config = new Config();
config.setShowLog(true);  //是否打印日志
//以下参数由服务方提供：
config.setAsrServerUrl(""); // 语音识别服务器地址
config.setTtsServerUrl("");  // 语音合成服务器地址
config.setWebServerUrl("");  // 网页链接
SpeechSDK.startSpeech(context,config);
```
