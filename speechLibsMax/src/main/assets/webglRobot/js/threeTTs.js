(function () {
    var INDEX = 0;
    var ACTION1 = '320';
    var LIGHTCOLOR = '';
    function ThreeTTS() {
        this.isDebug = window.location.href.split('?')[1] == 'openDebugFps';
        this.camera = null; // 相机
        this.renderer = null; // 渲染器
        this.scene = null; // 场景
        this.light = null; // 灯光
        this.hostModle = null; // 主持人模型
        this.positionData = {}; // 位置信息
        this.group = new THREE.Group();
        // 初始化容器
        this.container = document.createElement('div');
        document.body.appendChild(this.container);
        this.mixers = null; // 模型动画
        this.clock = new THREE.Clock();
        this.viewData = []; // 模型数据
        this.stats = null; // FPS调试器
        this.bs = ['mouthUpperUpRight', 'jawOpenMouthClose', 'mouthDimpleRight', 'mouthPressLeft', 'mouthPressRight', 'mouthRollUpper', // 0-5
            'mouthRollLower', 'mouthSmileRight', 'mouthSmileLeft', 'mouthFrownLeft', 'mouthShrugUpper', 'mouthShrugLower', 'mouthPucker', 'mouthFunnel', // 6-13
            'mouthFrownRight', 'mouthDimpleLeft', 'mouthLeft', 'mouthStretchRight', 'mouthStretchLeft', 'mouthRight', 'mouthLowerDownLeft', 'mouthLowerDownRight', // 14-21
            'mouthUpperUpLeft', 'jawOpen', 'jawRight', 'jawLeft', 'jawForward'];
        this.actions = {};
        this.isLiftHand = false;
        this.animationLock = null; // 动画的开关锁 ;
        this.previousAction = {}; // 上一个动作 ;
        this.activeAction = {}; // 当前激活的动作 ;
        this.firstRender = true ; // 首次渲染 ;
    }

    ThreeTTS.prototype = {
        constructor: ThreeTTS,
        inidatGui: function (controler) {
            var gui = new dat.GUI();
            for (key in controler) {
                if (key === 'color') {
                    gui.addColor(controler, key);
                }
                else {
                    for (key2 in controler[key]) {
                        gui.add(controler[key], key2, -400, 400);
                    }
                }
            }
        },
        initStats: function () {
            this.stats = new Stats();
            this.container.appendChild(this.stats.domElement);
        },
        initCamera: function (position) {
            // 初始化相机
            // this.camera = new THREE.PerspectiveCamera( 45, 8 / 10, 1, 20000 );
            // this.camera = new THREE.PerspectiveCamera( 45, 1240 / 720, 1, 20000 );
            // this.camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 20000);
            //var temp = window.innerWidth / 1242 * 11;
            //this.camera = new THREE.OrthographicCamera(window.innerWidth / -temp, window.innerWidth / temp, window.innerHeight / temp, window.innerHeight / -temp, -200, 500);
            var scale = 12 ;
            this.camera = new THREE.OrthographicCamera(1242 / -scale, 1242 / scale, 720 / scale, 720 / -scale, -200, 500);
            this.camera.position.set(position.cameraPosition.x, position.cameraPosition.y, position.cameraPosition.z);
            this.camera.lookAt(new THREE.Vector3(position.focusPosition.x, position.focusPosition.y, position.focusPosition.z));
        },
        initScene: function () {
            this.scene = new THREE.Scene();
            this.light = new THREE.HemisphereLight(LIGHTCOLOR || '#ffffff', LIGHTCOLOR || '#ffffff');
            this.light.position.set(0, 200, 100);
            this.scene.add(this.light);
        },
        intiRenderer: function () {
            this.renderer = new THREE.WebGLRenderer({antialias: true, alpha: true,preserveDrawingBuffer:true});
            this.renderer.setClearAlpha(0);
            this.renderer.setPixelRatio(window.devicePixelRatio);
            //this.renderer.setSize(1242 / 2, 720 / 2);
            this.renderer.setSize(window.innerWidth, window.innerHeight);
            this.renderer.shadowMap.enabled = true;
            this.container.appendChild(this.renderer.domElement);
            document.querySelector('canvas').style.display = 'block';
        },
        activateAllActions: function (action, weight) {

            this.setWeight(action, weight);
            action.play();

        },
        setWeight: function (action, weight) {
            action.enabled = true;
            action.setEffectiveTimeScale(1);
            action.setEffectiveWeight(weight);
        },
        initLoader: function (data, modelPosition) {
            var loader = new THREE.FBXLoader();
            try {
                var clip;
                var action = {};
                nativeLog('base64开始解析================xxxxxxxx==================================================>')
                var bufferData = this.base64DecToArr(data).buffer ;
                nativeLog('模型开始解析===================xxxxxxxx================================================>')
                this.hostModle = loader.parse(bufferData, 'http://baodi.com');
                nativeLog('模型解析结束===================xxxxxxxx================================================>')
                this.hostModle.position.set(modelPosition.x, modelPosition.y, modelPosition.z);
                this.hostModle.mixer = new THREE.AnimationMixer(this.hostModle);
                this.mixers = this.hostModle.mixer;
                for (var i = 0; i < this.hostModle.animations.length; i++) {
                    clip = this.hostModle.animations[i];
                    action = this.hostModle.mixer.clipAction(clip);
                    console.log('动画名称===================================>' + clip.name);
                    // 让动画在最后一帧的时候自动暂停
                    this.actions[clip.name] = action;
                }
                if(this.hostModle.children.length >= 4){
                    this.group.add(this.hostModle) ;
                    this.scene.add(this.group);
                }else {
                    throwError({status: 5, msg: '解析模型不完整', error: ''});
                }
            }
            catch (error) {
                throwError({status: 0, msg: '加载模型失败,请检查模型数据是否正确', error: error.toString()});
            }

        },
        renderModel: function (positionData) {
            requestAnimationFrame(this.renderModel.bind(this, positionData));
            this.renderer.render(this.scene, this.camera);
            if(this.firstRender){
                this.firstRender = false ;
                var self = this ;
                // setTimeout(function(){
                //     var canvas3D = self.renderer.domElement.getContext("webgl") ;
                //     var img2D =new Image() ;
                //
                //     var canvas2D = document.createElement('canvas') ;
                //
                //     canvas2D.style.height = window.innerHeight + 'px';
                //     canvas2D.style.width = window.innerWidth + 'px';
                //
                //     canvas2D.height = canvas3D.drawingBufferHeight ;
                //     canvas2D.width = canvas3D.drawingBufferWidth ;
                //     canvas2D.style.display = 'none' ;
                //
                //     img2D.style.height = canvas3D.drawingBufferHeight ;
                //     img2D.style.width = canvas3D.drawingBufferWidth ;
                //
                //     document.body.appendChild(canvas2D) ;
                //
                //     img2D.src = self.renderer.domElement.toDataURL("img/png");
                //
                //     img2D.addEventListener("load", function() {
                //         var data,temp;
                //         canvas2D.getContext('2d').drawImage(img2D, 0, 0);
                //         data = canvas2D.getContext('2d').getImageData(0,0,canvas3D.drawingBufferWidth,canvas3D.drawingBufferHeight).data ;
                //         temp = data.slice(data.length - canvas3D.drawingBufferWidth * 4) ;
                //
                //         if(temp.some(function(value){ return value > 0 })) {
                //             if (/iPhone/.test(window.navigator.userAgent)) {
                //                 // 通知 ios 已经可以渲染数据了 ;
                //                 window.webkit.messageHandlers.h5ModelReady.postMessage(versionCode);
                //             }
                //             else if (/Android/.test(window.navigator.userAgent)) {
                //                 // 通知 android 已经可以渲染数据了
                //                 window.android.h5ModelReady(versionCode);
                //             }
                //             else {
                //                 console.log('web环境');
                //             }
                //         }else {
                //             throwError({status: 5, msg: '解析模型不完整', error: ''});
                //         }
                //     });
                // },100)

                if (/iPhone/.test(window.navigator.userAgent)) {
                    // 通知 ios 已经可以渲染数据了 ;
                    window.webkit.messageHandlers.h5ModelReady.postMessage(versionCode);
                }
                else if (/Android/.test(window.navigator.userAgent)) {
                    // 通知 android 已经可以渲染数据了
                    window.android.h5ModelReady(versionCode);
                }
                else {
                    console.log('web环境');
                }

                nativeLog('init ============================xxxxxxxx===========================> 模型渲染完成');
            }

            if (this.isDebug) {
                this.camera.position.set(positionData.cameraPosition.x, positionData.cameraPosition.y, positionData.cameraPosition.z);
                this.hostModle.position.set(positionData.modelPosition.x, positionData.modelPosition.y, positionData.modelPosition.z);
                this.camera.lookAt(positionData.focusPosition.x, positionData.focusPosition.y, positionData.focusPosition.z);
                this.light.color = new THREE.Color(positionData.color);
                this.light.groundColor = new THREE.Color(positionData.color);
            }
        },
        setViewData: function (value) {
            this.viewData = this.viewData.concat.apply(this.viewData, value.data);
        },
        decodeBinaryData: function (buffers) {
            return new Float32Array(buffers);
        },
        base64DecToArr: function (sBase64, nBlockSize) {
            var sB64Enc = sBase64.replace(/[^A-Za-z0-9\+\/]/g, '');
            var nInLen = sB64Enc.length;
            var nOutLen = nBlockSize ? Math.ceil((nInLen * 3 + 1 >>> 2) / nBlockSize) * nBlockSize : nInLen * 3 + 1 >>> 2;
            var aBytes = new Uint8Array(nOutLen);

            for (var nMod3, nMod4, nUint24 = 0, nOutIdx = 0, nInIdx = 0; nInIdx < nInLen; nInIdx++) {
                nMod4 = nInIdx & 3;
                nUint24 |= this.b64ToUint6(sB64Enc.charCodeAt(nInIdx)) << 18 - 6 * nMod4;
                if (nMod4 === 3 || nInLen - nInIdx === 1) {
                    for (nMod3 = 0; nMod3 < 3 && nOutIdx < nOutLen; nMod3++, nOutIdx++) {
                        aBytes[nOutIdx] = nUint24 >>> (16 >>> nMod3 & 24) & 255;
                    }
                    nUint24 = 0;
                }

            }
            return aBytes;
        },
        b64ToUint6: function (nChr) {
            return nChr > 64 && nChr < 91 ? nChr - 65 : nChr > 96 && nChr < 123 ? nChr - 71 : nChr > 47 && nChr < 58 ? nChr + 4 : nChr === 43 ? 62 : nChr === 47 ? 63 : 0;
        },
        playView: function (value) {
            var valtmp = 0;
            var checkLip = false;
            var self = this;

            if (!this.animationLock) {
                self.clock.start();

                if (!self.isLiftHand) {
                    self.actions[ACTION1]
                        .reset()
                        .setEffectiveWeight(1)
                        .fadeIn(0)
                        .play();
                }

                this.animationLock = setInterval(function () {
                    // console.log('==========================================' + self.clock.getDelta()) ;
                    self.mixers.update(self.clock.getDelta());
                }, 33);
            }

            value[23] = 0.33 * value[10] + 0.33 * 0.5 * value[20] + 0.33 * value[23];
            for (var i = 0; i < this.bs.length; i++) {
                var val0 = value[i];
                if (val0 < 0) {
                    val0 = 0;
                }

                valtmp = Math.pow(val0, 2) * 5;

                if(i == 23){
                    valtmp = Math.pow(val0,3) * 20 ;
                }

                if (i == 0 || i == 22) {
                    val0 = Math.max((value[0] + value[22]) * 0.75, 0);
                }

                if (i == 7 || i == 8) {
                    valtmp = valtmp * 0.6;
                }

                if (i == 10) {
                    if (valtmp - value[21] * value[21] * 5 > 0.03) {
                        checkLip = true;
                        // val0 *= 0.1;
                        // valtmp = val0 * val0 * 5;
                    }
                    val0 = value[23] * 0.3;
                    valtmp = val0 * val0 * 5;

                    while (valtmp > 0.3) {
                        valtmp = valtmp * 0.5;
                    }
                }

                if (i == 11) {
                    while (valtmp > 0.2) {
                        valtmp = valtmp * 0.5;
                    }
                }

                if (i == 20 || i == 21) {
                    val0 = Math.max((value[20] + value[21]) * 0.75, 0);
                    if (checkLip && valtmp > 0) {
                        // val0 += 0.1;
                        val0 *= 1.5;
                        valtmp = val0 * val0 * 5;
                    }

                    while (valtmp > 0.5) {
                        valtmp *= 0.5;
                    }
                }

                if (i == 23) {
                    // if (valtmp > 0.5) {
                    //     valtmp = valtmp * 0.5;
                    // }

                    //if (checkLip && (valtmp > 0)) {
                    //    // val0 += 0.1;
                    //    val0 *= 1.5;
                    //    valtmp = val0 * val0 * 5;
                    //}

                    while (valtmp > 0.3) {
                        valtmp *= 0.5;
                    }
                }

                if (valtmp > 1) {
                    valtmp = 1;
                }

                this.hostModle.children[INDEX].morphTargetInfluences[i] = valtmp;
            }

            if (this.stats) {
                this.stats.update();
            }

        },
        playAnimate: function (fn) {
            if (this.viewData.length >= 51) {
                fn(this.viewData.slice(0, 51));
                this.playView(this.viewData.slice(0, 51));
                this.viewData = this.viewData.slice(51);
            }
            else {
                fn('缓存唇动数据为空');
                this.viewData = [];
            }
        },
        clearAnimateBuffer: function () {
            this.viewData = [];
            this.resetLip();
        },
        resetModel: function () {
            var self = this;
            this.viewData = [];

            this.actions[ACTION1].fadeOut(0.5).play();

            if (this.actions.armR) {
               this.actions.armR.fadeOut(0.5).play();
            }

            setTimeout(function () {
                self.clock.stop();
                if (self.animationLock) {
                    clearInterval(self.animationLock);
                }

                self.animationLock = null;
            }, 520);

            this.resetLip();
        },
        resetLip: function () {
            for (var i = 0; i < (this.bs.length + 2); i++) {
                this.hostModle.children[INDEX].morphTargetInfluences[i] = 0.0;
            }
        },
        nativeEmitEvent: function (name, params) {

            var self = this;

            switch (name) {
                case 'liftHand':

                    if (this.isLiftHand || !this.actions.armR) {
                        return;
                    }

                    this.actions[ACTION1].fadeOut(0.3);

                    this.actions.armR
                        .reset()
                        .setEffectiveTimeScale(1)
                        .setEffectiveWeight(1)
                        .fadeIn(0.3)
                        .play();

                    this.isLiftHand = true;
                    setTimeout(function () {
                        self.actions.armR.stop();
                        self.isLiftHand = false;

                        self.actions[ACTION1]
                            .reset()
                            .setEffectiveTimeScale(1)
                            .setEffectiveWeight(1)
                            .fadeIn(0.1)
                            .play();

                    }, 2700);

                    break;
                case 'resetLip':
                    self.resetLip();
                    break;
                case 'lightOff':
                    LIGHTCOLOR = '#c3c3c3';
                    if (self.light) {
                        self.light.color = new THREE.Color('#c3c3c3');
                        self.light.groundColor = new THREE.Color('#c3c3c3');
                    }

                    break;
                case 'lightOn':
                    LIGHTCOLOR = '#ffffff';
                    if (self.light) {
                        self.light.color = new THREE.Color('#ffffff');
                        self.light.groundColor = new THREE.Color('#ffffff');
                    }

                    break;
                case 'changeLight':
                    LIGHTCOLOR = params;
                    if (self.light) {
                        self.light.color = new THREE.Color(params);
                        self.light.groundColor = new THREE.Color(params);
                    }

                    break;
                default:
                    console.log('==========>' + name + '事件没有注册');
            }
        },
        initAxieHelp: function () {
            var axes = new THREE.AxisHelper(300);
            this.scene.add(axes); // 轴
        },
        init: function (modelbase64Data, versionCode) {

            var positionData = {
                cameraPosition: {
                    x: 0,
                    y: 0,
                    z: 0
                },
                focusPosition: {
                    x: 0,
                    y: 0,
                    z: 0
                },
                modelPosition: {
                    x: 45,
                    y: 10,
                    z: 0
                },
                color: '#ffffff'
            };

            if (!modelbase64Data) {
                throwError({
                    status: 4,
                    msg: 'init 没有 传入模型数据'
                });
                return;
            }

            // if (params) {
            //     // JSON.parse(params)
            //     positionData = Object.assign(positionData, {});
            // }

            // 初始化相机
            this.initCamera(positionData);

            // 初始化场景
            this.initScene();

            // 初始化renderer
            this.intiRenderer();

            // 初始化模型数据
            this.initLoader(modelbase64Data, positionData.modelPosition);


            if (this.isDebug) {
                this.inidatGui(positionData);
                this.initAxieHelp();
            }

            document.body.removeChild(document.getElementsByClassName('loading')[0]);
            // 开始循化渲染动画
            this.renderModel(positionData);
        }
    };
    window.ThreeTTS = ThreeTTS;
})();
