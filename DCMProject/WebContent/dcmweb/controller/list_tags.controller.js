sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History'],
				function(Controller, History) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_tags",
									{	
										onNavigateBack : function(event) {
											var oHistory = History.getInstance();
											var sPreviousHash = oHistory.getPreviousHash();

											if (sPreviousHash !== undefined) {
												window.history.go(-1);
											} else {
												var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
												oRouter.navTo("Main", true);
											}
										},
										onInit : function(evt) {
										},
										onAdd : function(evt) {
											var oRouter = sap.ui.core.UIComponent
													.getRouterFor(this);
											oRouter.navTo("EditTag", {id:"0"});
										},
										onDetailPress : function(event) {
											var oRouter = sap.ui.core.UIComponent
													.getRouterFor(this);
											var loBindingContext = event.getSource().getBindingContext();
											var lvId = loBindingContext.getProperty("Id");
											oRouter.navTo("EditTag", {id:lvId});
										}
									});

					return PageController;
				});
