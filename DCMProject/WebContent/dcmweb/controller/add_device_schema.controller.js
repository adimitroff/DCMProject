//TO DO : when you want to create new schema it may keep the last edited schema id, and to load the edit page not the add page
// the same problem persist and in playlists.
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
						var loRouter = sap.ui.core.UIComponent
						.getRouterFor(this);
						var loRoute = loRouter.getRoute("EditSchema");
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
							path : "/DevicePropertyTypes(" + lvId + ")",
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

					handleDeletePress : function() {
						var oModel = this.getView().getModel();
						var lvId = this.getView().byId("id").getValue();
						oModel.remove("/DevicePropertyTypes(" + lvId + "L)", {success: this._fnSuccess, error: this._fnError});
						oModel.refresh();
						this.navigateBack();
					},

					handleSavePress : function(evt) {
						var oModel = this.getView().getModel();

						var vProperties = {};
						vProperties.Id = this.getView().byId("id").getValue();
						vProperties.Name = this.getView().byId("name").getValue();
						vProperties.Description = this.getView().byId("description").getValue();

						if (vProperties.Id == "") {
							var oEntry = {};
							oEntry.Name = vProperties.Name;
							oEntry.Description = vProperties.Description;
//							oModel.createEntry("/DevicePropertyTypes", vProperties);							
							oModel.createEntry("/DevicePropertyTypes", { properties: oEntry });
						} else {
							var oEntry = {};
							var mParameters = {};
							mParameters.context = this.getView().getBindingContext();
							mParameters.success = this._fnSuccess;
							mParameters.error = this._fnError;
							oModel.update("", vProperties, mParameters);
						}
						oModel.submitChanges({success: this._fnSuccess, error: this._fnError});
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
					},
					onAddProperties: function(){
						var lvId = this.getView().byId("id").getValue();
						
						if (!lvId){
							sap.m.MessageBox.show(
								      "Schema must be saved, before adding the Properties!", {
								          icon: sap.m.MessageBox.Icon.ERROR,
								          title: "Error",
								          actions: [sap.m.MessageBox.Action.CANCEL]
								      }
							);
							return;
						}
						var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
						oRouter.navTo("ListSchemaProperties", {id:lvId});
					}
			});

					return PageController;
		});