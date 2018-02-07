sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				'sap/ui/core/routing/History' ],
		function(Controller, History) {
			var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.add_playlist", {
		            onNavigateBack : function(evt) {
						this.navigateBack();
					},
					onInit : function(oEvent) {
						var loRouter = sap.ui.core.UIComponent
						.getRouterFor(this);
						var loRoute = loRouter.getRoute("EditPlaylist");
						if (loRoute !== undefined) {
							loRoute.attachMatched(this._onRouteMatched, this);
						}
					},
					_onRouteMatched : function (ioEvent) {
						var loArgs, loView;
						loArgs = ioEvent.getParameter("arguments");
						var lvId = loArgs.id;
						loView = this.getView();
						
						if (lvId == 0){
							this.clearScreenFields();
							return;
						}
						
						loView.bindElement({
							path : "/Playlists(" + lvId + ")",
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
						oModel.remove("/Playlists(" + lvId + "L)");
						oModel.refresh();
						this.navigateBack();
					},

					handleSavePress : function(evt) {
						var oModel = this.getView().getModel();

						var vProperties = {};
						vProperties.Id = this.getView().byId("id").getValue();
						vProperties.Name = this.getView()
								.byId("name").getValue();
						vProperties.Description = this.getView().byId(
								"description").getValue();
						vProperties.Def = this.getView().byId(
							"default").getSelected();
						vProperties.Active = this.getView().byId(
							"active").getSelected();
						vProperties.Priority = this.getView().byId("priority").getSelectedKey();
						vProperties.ValidFrom = this.getView().byId("idValidFrom").getDateValue();
						vProperties.ValidTo = this.getView().byId("idValidTo").getDateValue();
						
						if (vProperties.Id == "") {
							vProperties.Id = 0;
							oModel.createEntry("/Playlists", vProperties);

						} else {
							var oEntry = {};
							var mParameters = {};
							mParameters.context = this.getView()
									.getBindingContext();
							mParameters.success = this._fnSuccess;
							mParameters.error = this._fnError;
							oModel.update("", vProperties, mParameters);
						}
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
						this.clearScreenFields();
						var oHistory = History.getInstance();
						var sPreviousHash = oHistory.getPreviousHash();

						if (sPreviousHash !== undefined) {
							window.history.go(-1);
						} else {
							var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
							oRouter.navTo("Main", true);
						}
					},
					onAddMediaObjects: function(){
						var lvId = this.getView().byId("id").getValue();
						
						if (!lvId){
							sap.m.MessageBox.show(
								      "Playlist must be saved, before adding the Tags", {
								          icon: sap.m.MessageBox.Icon.ERROR,
								          title: "Error",
								          actions: [sap.m.MessageBox.Action.CANCEL]
								      }
							);
							return;
						}
						var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
						oRouter.navTo("ListPlaylistRelatedMediaContents", {id:lvId});
					},
					onAddSchedule: function(){
						var lvId = this.getView().byId("id").getValue();
						
						if (!lvId){
							sap.m.MessageBox.show(
								      "Playlist must be saved, before adding the Tags", {
								          icon: sap.m.MessageBox.Icon.ERROR,
								          title: "Error",
								          actions: [sap.m.MessageBox.Action.CANCEL]
								      }
							);
							return;
						}
						var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
						oRouter.navTo("ListPlaylistRelatedSchedules", {id:lvId});
					},
					clearScreenFields : function(){
						this.getView().byId("id").setValue("");
						this.getView().byId("name").setValue("");
						this.getView().byId("description").setValue("");
						
						this.getView().byId("default").selected = false;
						this.getView().byId("active").selected = false;
						
						this.getView().byId("priority").setValue("");
					},
					formatSelected: function(ivValue){
						return true;
						if (ivValue == 'true')
							return true;
						else
							return false;
					}
			});

					return PageController;
		});