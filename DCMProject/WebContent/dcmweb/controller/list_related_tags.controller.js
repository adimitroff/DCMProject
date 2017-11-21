sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History',
				  'sap/m/MessageToast'],
				function(Controller, History, MessageToast) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_related_tags",
									{	
										relPath:"",
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
											var loRouter = sap.ui.core.UIComponent.getRouterFor(this);
											var loRoute = loRouter.getRoute("ListMediaContentRelatedTags");
											if (loRoute !== undefined) {
												this.relPath = "/MediaContents(&1L)";
												loRoute.attachMatched(this._onRouteMatched, this);
											} else {
												var loRoute = loRouter.getRoute("ListDeviceRelatedTags");
												if (loRoute !== undefined) {
													this.relPath = "/Devices(&1L)";
													loRoute.attachMatched(this._onRouteMatched, this);
												}
											}
										},
										_onRouteMatched : function (ioEvent) {
											var loArgs, loView;
											loArgs = ioEvent.getParameter("arguments");
											var lvId = loArgs.id;
											//loView = this.getView();
											var loTable = this.getView().byId("idTagsTable");
											this.relPath = this.relPath.replace("&1",lvId);
											//loTable.bindItems(this.path);
											//loTable.bindItems("/Tags");
//											loView.bindElement({
//												path : this.path,
//												events : {
//													change: this._onBindingChange.bind(this),
//													dataRequested: function (oEvent) {
//														loView.setBusy(true);
//													},
//													dataReceived: function (oEvent) {
//														loView.setBusy(false);
//													}
//												}
//											});
										},
										onAdd : function(evt) {
											if (!this._oDialog) {
												this._oDialog = sap.ui.xmlfragment("net.cb.dcm.frontend.fragment.tag_popup_table", this);
											}

											this.getView().addDependent(this._oDialog);

											// toggle compact style
											jQuery.sap.syncStyleClass("sapUiSizeCompact", this.getView(), this._oDialog);
											this._oDialog.open();
										},
										onDelete : function(event){
											//var loBindingContext = event.getSource().getBindingContext();
											//var lvId = loBindingContext.getProperty("Id");
											
											var oItem = event.getParameter("listItem");
											var aCells = oItem.getCells();
											var lvId = aCells[0].getTitle();
											
											
											var oModel = this.getView().getModel();
											var lvDeletePath = this.relPath + "/$links/TagDetails" + "(" + lvId + "L)"
											oModel.remove(lvDeletePath);
											oModel.refresh();
										},
										handlePopupSearch: function(oEvent) {
											var sValue = oEvent.getParameter("value");
											var oFilter = new Filter("Name", sap.ui.model.FilterOperator.Contains, sValue);
											var oBinding = oEvent.getSource().getBinding("items");
											oBinding.filter([oFilter]);
										},

										handlePopupClose: function(oEvent) {
											var aContexts = oEvent.getParameter("selectedContexts");
											if (aContexts && aContexts.length) {
												var lvId = aContexts.map(function(oContext) { return oContext.getObject().Id; }).join(", ");
												var oTable = this.byId("idTagsTable");
												var oModel = this.getView().getModel();

												var sItems = oTable.getItems();
												
												if (sItems.length != 0) {
													for ( var i = sItems.length - 1; i >= 0; i--) {
														var aCells = sItems[i].getCells();
														var lvTmpId = aCells[0].getTitle();
														if (lvId == lvTmpId){
															MessageToast.show("The tag choosen already existst in the table. No need to add it again!");
															return;
														}							
													}
												}
												var lvUrl = "http://localhost:8080/DCMProject/DCMService.svc" + 
													this.relPath + "/$links/TagDetails";
												var lvNewTagUri = "http://localhost:8080/DCMProject/DCMService.svc" +
													"/Tags(" + lvId + "L)";
												var aData = jQuery.ajax({
													type : "POST",
													contentType : "application/json",
													data: '{ "uri": "' + lvNewTagUri + '"}',
													url : lvUrl,
													dataType : "json",
													async: false,
													success : function(data,textStatus, jqXHR) {
														sap.m.MessageToast.show("Success",{
															closeOnBrowserNavigation: false });
													},
													error : function(response) {
														sap.m.MessageToast.show("Error",{
															closeOnBrowserNavigation: false });
													},
												});
												/*
												var vProperties;
												vProperties = { "uri": "http://localhost:8080/DCMProject/DCMService.svc/Tags(101L)" };
												oModel.createEntry("/MediaContents(2L)/$links/TagDetails", vProperties);
												oModel.submitChanges(this._fnSuccess, this._fnError);
												*/
												oModel.refresh();
												return;
											}
											oEvent.getSource().getBinding("items").filter([]);
										},
										_fnSuccess : function() {
											//jQuery.sap.require("sap.m.MessageToast");
											sap.m.MessageToast.show("Success",{
												closeOnBrowserNavigation: false });
										},
										_fnError : function(response) {
											//jQuery.sap.require("sap.m.MessageToast");
											sap.m.MessageToast.show("Error",{
												closeOnBrowserNavigation: false });
										}
									});

					return PageController;
				});
