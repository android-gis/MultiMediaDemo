cordova.define("cordova-plugin-multimedia.MultiMediaPlugin", function(require, exports, module) {
function MultiMediaPlugin() {
  var exec = require("cordova/exec");
}

MultiMediaPlugin.prototype.doOpen = function (successCallback, errorCallback, options) {
        var str = {};
        str.type = 3;//1 快速拍照  2 多图选择  3  图片预览
        var arrays = new Array();
        options.isCanDelete =options.isCanDelete||true;
        arrays=options.localFileRequestHeader.split("/");
        str.folderName =arrays[arrays.length-1];
        str.isCanDelete =options.isCanDelete;
           var dataobj = {};
           var pics = [];
           if(options.data){
                dataobj = options.data;
                for(var i=0;i<dataobj.length;i++){
                     var pic = {};
                     var fileName =  dataobj[i].localFileName;
                     dataobj[i].fileUrl =dataobj[i].fileUrl||"unknow"
                     var fileName_www = dataobj[i].fileUrl;
                     pic = {
                          fileName :  fileName ,
                          filePath_www :fileName_www
                     };
                     pics.push(pic);
              }
           }
           str.data = pics;
	  cordova.exec(successCallback, errorCallback, "MultiMediaPlugin", "", str);
};

MultiMediaPlugin.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.MultiMediaPlugin = new MultiMediaPlugin();
  return window.plugins.MultiMediaPlugin;
};

cordova.addConstructor(MultiMediaPlugin.install);

});
