//     var isStopPlayAnimate = false ;
// document.querySelector('.btn').addEventListener('click', function () {
//     isStopPlayAnimate = true ;
//     renderView.nativeEmitEvent('resetLip');
// });
var isDebug = window.location.href.split('?')[1] === 'openDebugFps';

var renderView;

var versionCode = 'versionCode:1.5';

console.log('h5 version======================>' + versionCode);

/* globals WEBGL */
if (WEBGL.isWebGLAvailable() === false) {
    throwError({
        status: 3,
        msg: '浏览器内核不支持wegGl',
        error: ''
    });

    /* globals WEBGL */
    document.body.appendChild(WEBGL.getWebGLErrorMessage());
}
else {

    /* globals ThreeTTS */
    renderView = new ThreeTTS();

    if (isDebug) {
        document.getElementsByClassName('loading')[0].innerHTML = '<span>模型正在加载....</span>';
        renderView.initStats();
    }
}

/*
     * @method 初始化3d模型
     * @params {String,Object} modelBuffer :base64 String positionParams: 相机位置信息 cameraPosition focusPosition modelPosition ;
     * @return 无
     */
function init(modelBuffer, positionParams) {

    nativeLog('init =======================xxxxxxxx================================> 调用');

    renderView.init(modelBuffer, versionCode);

    window.addEventListener('resize', onWindowResize);
    function onWindowResize() {
        renderView.camera.aspect = window.innerWidth / window.innerHeight;
        renderView.camera.updateProjectionMatrix();
        renderView.renderer.setSize(window.innerWidth, window.innerHeight);
        renderView.renderer.render(renderView.scene, renderView.camera);
    }
}

/*
    * @method 播放一帧的动画数据
    * @params 无
    * @return 无
    * */
function playAnimate() {
    if (renderView) {
        try {
            renderView.playAnimate(function (value) {
                 nativeLog('唇动数据:' + JSON.stringify(value));
            });
        }
        catch (error) {
            throwError({
                status: 2,
                msg: '渲染模型失败',
                error: error.toString()
            });
        }
    }
}

/*
    * @method 清除本地缓存的唇动数据
    * @params 无
    * return 无
    * */
function clearAnimateBuffer() {
    nativeLog('clearAnimateBuffer:调用');
    renderView.clearAnimateBuffer();
}

/*
    * @method 重置模型 包括嘴型 眉毛 眼睛 身体
    * @params 无
    * @return 无
    * */
function resetModel() {
    nativeLog('resetModle:调用');
    renderView.resetModel();
}

/*
    * @method native 向h5透传唇动数据
    * @params {String base64} value: 每包的唇动系数; base64字符串
    * @return 无
    * */
function setViewData(value) {
    try {
        if (value && value !== 'null') {

            var valueBuffer = renderView.base64DecToArr(value).buffer;
            renderView.setViewData({
                data: renderView.decodeBinaryData(valueBuffer)
            });
        }

    }
    catch (error) {
        throwError({
            status: 1,
            msg: '解析唇动数据失败,请确认唇动数据是否正确',
            error: error.toString()
        });
    }
}

/*
    * @method 抛出错误
    * @params { Object } err 错误对象
    * @return 无
    * */
function throwError(err) {
    if (/iPhone/.test(window.navigator.userAgent)) {
        // 加载模型失败 ;
        window.webkit.messageHandlers.onError.postMessage(JSON.stringify(err));
    }
    else {
        // 加载模型失败 ;
        window.android.onJsError(JSON.stringify(err));
    }
}

/*
    * @method 打印log
    * @params {String} value: 需要打印的log
    * @return 无
    * */
function nativeLog(value) {
    try {
        if (/iPhone/.test(window.navigator.userAgent)) {
            window.webkit.messageHandlers.nativeLog.postMessage(value);
        }
        else {
            console.log(value);
        }
    }
    catch (e) {
        console.log('nativeLog--error:=============================>' + JSON.stringify(e));
    }
}

/*
    * @method native向H5广播事件
    * @params {String,Object}  eventName : 事件名称; params : 事件参数
    * @return 无
    * */
function nativeEmitEvent(eventName, params) {
    nativeLog('nativeEmitEvent:name=' + eventName + ',params:' + params);
    renderView.nativeEmitEvent(eventName, params);
}

if (/Android/.test(window.navigator.userAgent)) {
    // 通知 android 可以加载模型
    window.android.h5PageReady('ready');
}

//init(base64Data) ;
