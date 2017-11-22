sap.ui
		.define(
				[ 'sap/ui/core/mvc/Controller',
				'sap/ui/core/routing/History',
				'sap/m/MessageToast',
				'sap/m/MessageBox'],
		function(Controller, History) {
			var PageController = Controller
							.extend(
									"net.cb.dcm.frontend.controller.add_media_object", {
		            onNavigateBack : function(evt) {
		            	this.clearScreenFields();
						this.navigateBack();
					},
					onInit : function(oEvent) {
						var loRouter = sap.ui.core.UIComponent
						.getRouterFor(this);
						var loRoute = loRouter.getRoute("EditMediaObject");
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
							path : "/MediaContents(" + lvId + ")",
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
						oModel.remove("/MediaContents(" + lvId + "L)");
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
						vProperties.MediaType = this.getView().byId(
								"media_type").getValue();
						vProperties.Duration = this.getView().byId(
								"duration").getValue();
						vProperties.FilePath = this.getView().byId(
							"file").getValue();
						if (vProperties.FilePath == undefined ||
								  vProperties.FilePath == "") {
							sap.m.MessageBox.show(
								      "File must be selected", {
								          icon: sap.m.MessageBox.Icon.ERROR,
								          title: "Error",
								          actions: [sap.m.MessageBox.Action.CANCEL]
								      }
							);
							return;
						}
						if (vProperties.Name == undefined || vProperties.Name == ""){
							sap.m.MessageBox.show(
						      "Field Name cannot be empty.", {
						          icon: sap.m.MessageBox.Icon.ERROR,
						          title: "Error",
						          actions: [sap.m.MessageBox.Action.CANCEL]
						      }
						    );
							return;
						}
						if (vProperties.Id == "") {
							vProperties.Id = 0;
							oModel.createEntry("/MediaContents", vProperties);

						} else {
							var mParameters = {};
							mParameters.context = this.getView()
									.getBindingContext();
							mParameters.success = this._fnSuccess;
							mParameters.error = this._fnError;
							
							vProperties.TagDetails = "{ __metadata: { [{uri: "//Tags(51L)"} ] }}"
							oModel.update("", vProperties, mParameters);
						}
						oModel.submitChanges(this._fnSuccess, this._fnError);
						oModel.refresh();
						var oFileUploader = this.getView().byId("fileUploader");
						oFileUploader.upload();
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
					handleUploadComplete: function(oEvent) {
						var sResponse = oEvent.getParameter("response");
						if (sResponse) {
							var sMsg = "";
							var m = /^\[(\d\d\d)\]:(.*)$/.exec(sResponse);
							if (m[1] == "200") {
								sMsg = "Return Code: " + m[1] + "\n" + m[2] + "(Upload Success)";
								oEvent.getSource().setValue("");
							} else {
								sMsg = "Return Code: " + m[1] + "\n" + m[2] + "(Upload Error)";
							}

							MessageToast.show(sMsg);
						}
					},
					handleTypeMissmatch: function(oEvent) {
						var aFileTypes = oEvent.getSource().getFileType();
						jQuery.each(aFileTypes, function(key, value) {aFileTypes[key] = "*." +  value;});
						var sSupportedFileTypes = aFileTypes.join(", ");
						MessageToast.show("The file type *." + oEvent.getParameter("fileType") +
												" is not supported. Choose one of the following types: " +
												sSupportedFileTypes);
					},
					handleValueChange: function(oEvent) {
						if (oEvent.mParameters.newValue){
						this.getView().byId(
							"file").setValue(oEvent.mParameters.newValue);
						}
					},
					onAddTags: function(){
						var lvId = this.getView().byId("id").getValue();
						
						if (!lvId){
							sap.m.MessageBox.show(
								      "Media Object must be saved, before adding the Tags", {
								          icon: sap.m.MessageBox.Icon.ERROR,
								          title: "Error",
								          actions: [sap.m.MessageBox.Action.CANCEL]
								      }
							);
							return;
						}
						var oRouter = sap.ui.core.UIComponent.getRouterFor(this);
						oRouter.navTo("ListMediaContentRelatedTags", {id:lvId});
					},
					clearScreenFields : function(){
						this.getView().byId("id").setValue("");
						this.getView().byId("name").setValue("");
						this.getView().byId("description").setValue("");
						this.getView().byId("media_type").setValue("");
						this.getView().byId("duration").setValue("");
						this.getView().byId("file").setValue("");
					}
			});

					return PageController;
		});