seajs.config({
	alias: {
		"jquery": "js/jquery-1.9.1.min.js",
		"bootstrap": "js/bootstrap.min.js",
		"validate": "libs/validate/jquery.validate.min.js",
		"message": "libs/validate/jquery.validate.addMethod.js",
		"common":"js/page/common.js?t=552225",
		"main":"js/page/main.js",
		"home":"js/page/home.js",
		"JGrid":"libs/JGrid/jquery.JGrid.js",
		"layer":"libs/layer/layer.js",
		"AddModel":"js/page/system/addModel.js?t=2014544424",
		"updateModel":"js/page/system/updateModel.js?t=2000",
		"modelList":"js/page/system/modelList.js",
		"addRegion":"js/page/system/addRegion.js?t=2014544424",
		"updateRegion":"js/page/system/updateRegion.js?t=2000",
		"regionList":"js/page/system/regionList.js",
		"articleList":"js/page/web/articleList.js",
		"announList":"js/page/web/announList.js",
		"siteList":"js/page/web/siteList.js",
		"addSite":"js/page/web/addSite.js?t=2000"
	},
	preload: ['jquery']
});