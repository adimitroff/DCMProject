sap.ui.define([ 'sap/ui/core/UIComponent' ], function(UIComponent) {
	"use strict";

	var Component = UIComponent.extend("net.cb.dcm.frontend.component",{
//		metadata : {
//			rootView : "net.cb.dcm.frontend.view.main",
//			dependencies : {
//				libs : [ "sap.m" ]
//			},
//			includes : [ "css/style.css" ],
//		}
		metadata : {
			manifest: "json"
		},
		init : function() {
			// call the init function of the parent
			UIComponent.prototype.init.apply(this, arguments);
			// create the views based on the url/hash
			this.getRouter().initialize();
			
			
			var loI18nModel = this.getModel("i18n");
			sap.ui.getCore().setModel(loI18nModel, 'i18n');
			var loModel = new sap.ui.model.odata.ODataModel("DCMService.svc/");
			sap.ui.getCore().setModel(loModel);
//			this.getRouter().attachRoutePatternMatched(this.handle, this)
//			
//			oCore = sap.ui.getCore();
//	        oConfig = oCore.getConfiguration();
//	 
//	        if (oConfig.getLanguage() !== lang) {
//	            oConfig.setLanguage(lang);
//	            
//	            oModel = new sap.ui.model.resource.ResourceModel({
//	                bundleUrl : sResourceBundlePath});
//	            oCore.setModel(oModel, 'i18n');
//	            oResourceBundle = oModel.getResourceBundle();
//	        }
		}

	});
	
	return Component;
});