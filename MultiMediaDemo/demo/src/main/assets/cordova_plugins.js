cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-multimedia/www/MultiMediaPlugin.js",
        "id": "cordova-plugin-multimedia.MultiMediaPlugin",
        "clobbers": [
            "window.plugins.MultiMediaPlugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-multimedia": "0.0.1"
};
// BOTTOM OF METADATA
});