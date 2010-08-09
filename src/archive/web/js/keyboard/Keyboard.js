zk.$package("keyboard");

//TODO 先留著等做完之後再考慮是否實做
//keyboard.Key = zk.$extends(zk.Widget,{
//
//});
//keyboard.ListItem = zk.$extends(zk.Widget,{
//
//});
keyboard.Keyboard = zk.$extends(zk.Widget, {
	KEYS_NUMERIC : [["1","2","3"],["4","5","6"],["7","8","9"],["0"]],
	KEYS_CHEWING :
		[["ㄅ","ㄉ","ˇ","ˋ","ㄓ","ˊ","˙","ㄚ","ㄞ","ㄢ","ㄦ"],
		 ["ㄆ","ㄊ","ㄍ","ㄐ","ㄔ","ㄗ","ㄧ","ㄛ","ㄟ","ㄣ"],
		 ["ㄇ","ㄋ","ㄎ","ㄑ","ㄕ","ㄘ","ㄨ","ㄜ","ㄠ","ㄤ"],
		 ["ㄈ","ㄌ","ㄏ","ㄒ","ㄖ","ㄙ","ㄩ","ㄝ","ㄡ","ㄥ"],
		 ["esc","space","backspace"]
		],
	KEY_CONTROLS:{"backspace":"_doBackspace","esc":"_doEsc"},
	_listing:false,
	_listPage:0,
	_listPageSize:10,
	_keyIndex:0,
	_spells:[],
	$define:{
		type:[
			function(value){
				  return value || "";
			}
		] ,
		activingComponentId : [
		    function(value){
		    	//TODO handle the problem with activing component situation
		    	//     there might need some onblue detect to remove textbox activing for keyboard
		    	// for exmaple , click one textbox1 and input something through keyboardA ,
		    	// and click the another textbox2 and input something with keyboardB,
		    	// now i am back to typing keyboardA , should keyboard  take textbox1 as activing compoent ?
		    	//

		    	// TODO get some notice that which active component state throught view;
		    	//   	may be add css class for view ......just thinking some way after.
		    	_spells = [];
		    	return value || "";
			}
		]
	},
	bind_ : function(evt) {

		var that = this;
		this.$supers('bind_', arguments);
		var node = this.$n();
		var zclass = this.getZclass();
		jq("." + zclass + "-key",node).each(function(){
			if(that.KEY_CONTROLS[this.innerHTML] != undefined){
				that.domListen_(this, "onClick", that.KEY_CONTROLS[this.innerHTML] );
			}else{
				that.domListen_(this, "onClick", '_doKeyPress');
			}
		});

		if( this._type == "chewing" ){
//			jq("." + zclass + "-list" , this.$n() ).hover(function(){
//				$(this).height("100px").css("overflow","scroll");
//				$( "." + zclass + "-listitem" , this ).show();
//			},function(){
//				$(this).height("").css("overflow","hidden");
//				that.updateListPaganiation(that._listPage, that._listPageSize);
//			});
			jq("." + zclass + "-list-paginiation-next" , this.$n() ).click(function(){
				that._listPage = (that._listPage + 1 ) % that.getListPageCount();
				that.updateListPaganiation(that._listPage, that._listPageSize);
			});
			jq("." + zclass + "-list-paginiation-pre" , this.$n() ).click(function(){
				var listCount = that.getListPageCount();
				that._listPage = ( listCount + that._listPage - 1 ) % listCount;
				that.updateListPaganiation(that._listPage, that._listPageSize);
			});
		}

	},
	unbind_ : function(evt) {
		this.$supers('unbind_', arguments);
	},

	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-keyboard" ;
	},
	_doItemPress : function(evt){
		this.appendValue( evt.domTarget.innerHTML );
		this.closeList();
	} ,
	_doKeyPress : function(evt){
		if(this._listing){	//開啟選字清單時不允許進行鍵盤輸入行為
			return false;
		}
		var keyValue = evt.domTarget.innerHTML;

		var activeComponent = (this._activingComponentId  && this.$f(this._activingComponentId)) || null;

		if(activeComponent == null){
			jq.alert("請先選擇輸入框", {icon: 'ERROR', title: "輸入錯誤"});
			return false;
		}

		if(this._type == "chewing"){
			this.fire("onWebKeyPress", { spells : this._spells , key : evt.domTarget.innerHTML });
		}else{
			activeComponent.setValue(activeComponent.getValue() + evt.domTarget.innerHTML );
		}

	},
	_doEsc : function(){
		if(this._type == "chewing"){
			if(this._listing){
				this.closeList();
			}else{
				this.clearSpell();
			}
		}
	},
	_doBackspace : function(){
		if(this._type == "chewing"){
			if(this._listing){	//開啟選字清單時不允許進行backspace 行為
				return false;
			}
			if(this._spells.length > 0 ){
				this.popSpell();
			}else{
				this.popValue();
			}
		}else{
			this.popValue();
		}
	},
	getSpellJQ:function(){
		return jq(">." + this.getZclass() + "-spell" , this.$n() );
	},
	getListJQ:function(){
		return jq(">." + this.getZclass() + "-list" , this.$n() );
	},
	getActivingComponent:function(){
		return (this._activingComponentId  && this.$f(this._activingComponentId)) || null;
	},
	checkActivingComponent:function(){
		var activingComponent = this.getActivingComponent();
		if(this.getActivingComponent() == undefined){
			jq.alert("請先選擇輸入框", {icon: 'ERROR', title: "輸入錯誤"});
			return null;
		}
		return activingComponent;
	},
	appendValue:function( value ){
		var activeComponent = this.getActivingComponent();
		activeComponent.setValue(activeComponent.getValue() + value );
	},
	popValue:function(){
		var activeComponent = this.getActivingComponent();
		var value = activeComponent.getValue();
		var newLength = (value.length - 1) > 0 ? (value.length - 1) : 0;
		activeComponent.setValue( value.substring(0, newLength ) );
	},
	errorSpell : function(){
		jq.alert("查無此拼音！", {icon: 'ERROR', title: "輸入錯誤"});
		this.clearSpell();
	},
	clearSpell:function(){
		this._spells = [];
		this.getSpellJQ().html("");
	},
	appendSpell : function(word){
		this._spells.push(word);
		this.getSpellJQ().html( this._spells.join("") );
	},
	popSpell : function(){
		this._spells.pop();
		this.getSpellJQ().html( this._spells.join("") );
	},
	openList : function(word,items){
		this.appendSpell(word);
		this.removeListItem();
		this.insertListItem(items);

		this.getListJQ().show();
		this._listing = true ;

	},
	closeList : function(){
		this.removeListItem();
		this.clearSpell();
		this.getListJQ().hide();
		this._listing = false ;
	},
	insertListItem : function(items){

		var zclass = this.getZclass();
		var jq_list = this.getListJQ();

		var html = [];
		this.drawList( html , items );
		jq_list.html( html.join("") ).show();


		this.updateListPaganiation(0,10);
		var controls = jq("." + zclass + "-list-paginiation-pre,." + zclass + "-list-paginiation-next,." + zclass + "-list-paginiation-state");
		controls.show();
		var that = this;
		jq("." + zclass + "-listitem" , this.$n() ).each(function(){
			that.domListen_( this, "onClick", "_doItemPress");
		});

	},
	removeListItem:function(){
		var zclass = this.getZclass();
		var jq_list = this.getListJQ();
		jq("." + zclass + "-listitem" , this.$n() ).remove();
		var controls = jq("." + zclass + "-list-paginiation-pre,." + zclass + "-list-paginiation-next,." + zclass + "-list-paginiation-state");
		controls.hide();
	},

	getListPageCount :function(){
		var zclass = this.getZclass();
		var listItems = jq("." + zclass + "-listitem" ,this.$n());
		var pageCount = Math.round( listItems.size() / this._listPageSize );
		return pageCount;
	},
	updateListPaganiation:function( pageNum ){
		var zclass = this.getZclass();
		var pageCount = this.getListPageCount();

		var listItems = jq("." + zclass + "-listitem" ,this.$n());
		listItems.hide();
		listItems.slice( pageNum * this._listPageSize , (pageNum + 1 ) * this._listPageSize ).show();

		function padding(num){
			return (num < 10) ? "0" + num : num ;
		};
		jq("." + zclass +"-list-paginiation-state",this.$n()).html( padding(pageNum+1) +"/"+ padding(pageCount) );
	},
	drawList:function(out,listItems){
		var zclass = this.getZclass();
		for ( var index = 0; index < listItems.length; index++) {
			var item = listItems[index];
			out.push("<li class='" , zclass , "-listitem'>" , item , "</li>");
		}
	},
	drawKey:function(out,keys){
		var zcs = this.getZclass();
		if(jq.isArray(keys)){

			out.push("<div class='",zcs,"-keyrow'>");
			for ( var index = 0; index < keys.length; index++) {
				this.drawKey(out,keys[index]);
			}
			out.push("</div>");

		}else{
			out.push("<div class='",zcs,"-key ",zcs,"-key-",this._keyIndex,"'>",keys,"</div>");
			this._keyIndex++;
		}
	},
	drawType: function(out){
		var keySet = [];
		this._keyIndex = 0 ;
		if(this._type == "numeric"){
			keySet = this.KEYS_NUMERIC;
		}else if(this._type == "chewing" ){
			keySet = this.KEYS_CHEWING;
		}

		for ( var index = 0; index < keySet.length; index++) {
			this.drawKey(out,keySet[index]);
		}


	}
});
