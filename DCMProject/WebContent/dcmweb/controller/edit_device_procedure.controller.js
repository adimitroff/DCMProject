sap.ui.define(
				[ 'sap/ui/core/mvc/Controller',
				'sap/ui/core/routing/History',
				'sap/m/MessageToast',
				'sap/m/MessageBox' ],
		function(Controller, History) {
			var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.add_device_schema", {
		            onNavigateBack : function(evt) {
						this.navigateBack();
					},
					onInit : function(oEvent) {
//						var loModel = new sap.ui.model.odata.ODataModel("DCMService.svc/");
//						this.getView().setModel(loModel);
						var loRouter = sap.ui.core.UIComponent.getRouterFor(this);
						var loRoute = loRouter.getRoute("EditDeviceProcedure");
						if (loRoute !== undefined) {
							loRoute.attachMatched(this._onRouteMatched, this);
						}
					},
					_onRouteMatched : function (ioEvent) {
						var loArgs, loView;
						loArgs = ioEvent.getParameter("arguments");
						var lvId = loArgs.id;
						loView = this.getView();

						loView.bindElement({
							path : "/DeviceProcedures(" + lvId + ")",
							events : {
								change: this._onBindingChange.bind(this),
								dataRequested: function (oEvent) {
									loView.setBusy(true);
								},
								dataReceived: function (oEvent) {
									loView.setBusy(false);
								}
							}
						});
					},
					_onBindingChange : function (oEvent) {
						// No data for the binding
//						if (!this.getView().getBindingContext()) {
//							this.getRouter().getTargets().display("notFound");
//						}
					},
					handleCancelPress : function() {
						this.navigateBack();
					},
					handleSavePress : function(evt) {
						var oModel = this.getView().getModel();

						var vProperties = {};
						vProperties.Id = this.getView().byId("id").getValue();
						vProperties.execTime = this.getView().byId("executionTime").getValue();
						
						var oEntry = {};
						var mParameters = {};
						mParameters.context = this.getView().getBindingContext();
						mParameters.success = this._fnSuccess;
						mParameters.error = this._fnError;
						oModel.update("", vProperties, mParameters);
							
						oModel.submitChanges(this._fnSuccess, this._fnError);
						oModel.refresh();
						this.navigateBack();

					},
					_fnSuccess : function() {
						jQuery.sap.require("sap.m.MessageToast");
						sap.m.MessageToast.show("Success",{
							    closeOnBrowserNavigation: false });
					},
					_fnError : function() {
						jQuery.sap.require("sap.m.MessageToast");
						sap.m.MessageToast.show("Error",{
						    closeOnBrowserNavigation: false });
					},
					navigateBack : function(){
						var oHistory = History.getInstance();
						var sPreviousHash = oHistory.getPreviousHash();

						if (sPreviousHash !== undefined) {
							window.history.go(-1);
						} else {
							var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
							oRouter.navTo("Main", true);
						}
					}
			});

					return PageController;
		});