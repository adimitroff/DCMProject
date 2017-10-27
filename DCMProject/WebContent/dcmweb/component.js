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
		}

	});
	return Component;
});