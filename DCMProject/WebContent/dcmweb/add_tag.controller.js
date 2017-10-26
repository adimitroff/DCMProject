sap.ui
		.controller(
				"dcmweb.add_tag",
				{
					handleNavButtonPress : function(evt) {
						app.back();
					},
					onInit : function(oEvent) {
						this
								.getView()
								.addDelegate(
										{
											onBeforeShow : function(evt) {
												if (this.direction != "back") {
													if (evt.data.oModel != null) {
														evt.to
																.setModel(evt.data.oModel);
														evt.to
																.setBindingContext(evt.data);
													} else {
														var oModel = new sap.ui.model.odata.ODataModel(
																"DCMService.svc/");
														evt.to.setModel(oModel);
														evt.to.byId("id")
																.setValue("");
														evt.to.byId("name")
																.setValue("");
														evt.to.byId(
																"description")
																.setValue("");
													}
												}
											}
										});
					},

					handleCancelPress : function() {
						app.back();
					},

					handleDeletePress : function() {
						var oModel = this.getView().getModel();
						var lvId = this.getView().byId("id").getValue();
						oModel.remove("/Tags(" + lvId + "L)");
						oModel.refresh();
						app.back();

					},

					handleSavePress : function(evt) {
						var oModel = this.getView().getModel();

						var vProperties = {};
						vProperties.Id = this.getView().byId("id").getValue();
						vProperties.Name = this.getView()
								.byId("name").getValue();
						vProperties.Description = this.getView().byId(
								"description").getValue();
						if (vProperties.Id == "") {
							vProperties.Id = 0;
							oModel.createEntry("Tags", vProperties);

						} else {
							var oEntry = {};
							var mParameters = {};
							mParameters.context = this.getView()
									.getBindingContext();
							mParameters.success = this._fnSuccess;
							mParameters.error = this._fnError;
							oEntry.Id = vProperties.Id;
							oEntry.Name = vProperties.Name;
							oEntry.Description = vProperties.Description;
							oModel.update("", oEntry, mParameters);
						}
						oModel.submitChanges(this._fnSuccess, this._fnError);
						oModel.refresh();

					},
					_fnSuccess : function() {
						jQuery.sap.require("sap.m.MessageToast");
						sap.m.MessageToast.show("Success");
						app.back();

					},
					_fnError : function() {
						jQuery.sap.require("sap.m.MessageToast");
						sap.m.MessageToast.show("Error");

					}

				});