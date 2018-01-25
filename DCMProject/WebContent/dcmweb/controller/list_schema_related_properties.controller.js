sap.ui.define(
				[ 'sap/ui/core/mvc/Controller',
				  'sap/ui/core/routing/History',
				  'sap/m/MessageToast'],
				function(Controller, History, MessageToast) {
					"use strict";

					var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.list_schema_related_properties",
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
											var loRoute = loRouter.getRoute("ListSchemaProperties");
											if (loRoute !== undefined) {
												loRoute.attachMatched(this._onRouteMatched, this);
											}
											
										},
										_onRouteMatched : function (ioEvent) {
											var loArgs, loView;
											loArgs = ioEvent.getParameter("arguments");
											var lvId = loArgs.id;
											this.relPath = "/DevicePropertyTypes(&1L)";
											this.relPath = this.relPath.replace("&1",lvId);
											this.updateTableBinding();
										},
										onAdd : function(evt) {
											if (!this._oDialog) {
												this._oDialog = sap.ui.xmlfragment("net.cb.dcm.frontend.fragment.properties_popup_table", this);
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
											var lvDeletePath = this.relPath + "/$links/PropertyDetails" + "(" + lvId + "L)"
											oModel.remove(lvDeletePath);
											oModel.refresh();
										},
										handlePopupSearch: function(oEvent) {
											var sValue = oEvent.getParameter("value");
											var oFilter = new sap.ui.model.Filter("Name", sap.ui.model.FilterOperator.Contains, sValue);
											var oBinding = oEvent.getSource().getBinding("items");
											oBinding.filter([oFilter]);
										},

										handlePopupClose: function(oEvent) {
											var aContexts = oEvent.getParameter("selectedContexts");
											if (aContexts && aContexts.length) {
												var lvId = aContexts.map(function(oContext) { return oContext.getObject().Id; }).join(", ");
												var oTable = this.byId("idPropertiesTable");
												var oModel = this.getView().getModel();

												var sItems = oTable.getItems();
												
												if (sItems.length != 0) {
													for ( var i = sItems.length - 1; i >= 0; i--) {
														var aCells = sItems[i].getCells();
														var lvTmpId = aCells[0].getTitle();
														if (lvId == lvTmpId){
															MessageToast.show("The property choosen already existst in the table. No need to add it again!");
															return;
														}							
													}
												}
												var lvUrl = "/DCMProject/DCMService.svc" + 
													this.relPath + "/$links/PropertyDetails";
												var lvNewPropertyUri = "/Propertys(" + lvId + "L)";
												var aData = jQuery.ajax({
													type : "POST",
													contentType : "application/json",
													data: '{ "uri": "' + lvNewPropertyUri + '"}',
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
												vProperties = { "uri": "/DCMProject/DCMService.svc/MediaContents(101L)" };
												oModel.createEntry("/MediaContents(2L)/$links/MediaContentDetails", vProperties);
												oModel.submitChanges(this._fnSuccess, this._fnError);
												*/
												oModel.refresh();
												return;
											}
											oEvent.getSource().getBinding("items").filter([]);
										},
										_fnSuccess : function() {
											sap.m.MessageToast.show("Success",{
												closeOnBrowserNavigation: false });
										},
										_fnError : function(response) {
											sap.m.MessageToast.show("Error",{
												closeOnBrowserNavigation: false });
										},
										updateTableBinding: function(){
											var oTable = this.getView().byId("idPropertiesTable"); 
											var oTemplate = new sap.m.ColumnListItem({
											    cells:[
											        new sap.m.ObjectIdentifier({title:"{Id}"}),
											        new sap.m.Text({text:"{Name}"}),
											        new sap.m.Text({text:"{Key}"})
											        ]
											});
											var path = this.relPath + "/PropertyDetails";
											oTable.bindItems(path,oTemplate);
										}
									});

					return PageController;
				});