zk.$package("keyboard");
keyboard.KeyboardArea = zk.$extends(zk.Widget, {
	bind_ : function(evt) {
		this.$supers('bind_', arguments);
	},
	unbind_ : function(evt) {
		this.$supers('unbind_', arguments);
	},
	appendChild : function(child){
		this.$supers("appendChild",arguments);
	}

});
