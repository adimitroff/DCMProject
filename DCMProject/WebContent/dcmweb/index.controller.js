sap.ui.controller("dcmweb.index", {
	onInit : function(evt) {

		this.getView().addDelegate(
				{
					onBeforeShow : function(evt) {
						var oModel = new sap.ui.model.odata.ODataModel(
								"DCMService.svc/");
						evt.to.setModel(oModel);
					}
				});
	},

	onAdd : function(evt) {
		app.to("addtag");
	},
	onDetailPress : function(event) {
		var bindingContext = event.getSource().getBindingContext();
		var flightid = bindingContext.getProperty("Id");
		var myObject = bindingContext.getObject();
		app.to("addtag", "slide", bindingContext);

	}
});