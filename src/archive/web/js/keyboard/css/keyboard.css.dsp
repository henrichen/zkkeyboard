<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-keyboard-key,.z-keyboard-key-empty{
	cursor:pointer;
	text-align:center;
	margin-top:1px;
	padding-top:15px;
	color:black;
	height:25px;
	float:left;
	display:block;
	font-size:16px;
}
.z-keyboard-key{
	padding-left:15px;
	padding-right:15px;
	border:1px solid #333333;
}
.z-keyboard-key-empty{
	width:15px;
}

.z-keyboard-keytable{
}
.z-keyboard-keyrow{
	display:block;
	overflow:auto;
	clear:both;
}
.z-keyboard-spell{
	width:85px;
	height:30px;
	font-size:16px;
	float:left;
}

.z-keyboard-list{
	display:none;
	background:white;
	width:300px;
	float:left;
	height:25px;
	list-style:none;
	overflow:hidden;
	margin:0;
	padding:0;
}
.z-keyboard-listitem{
	width:30px;
	height:25px;
	font-size:16px;
	color:black;
	float:left;
}

.z-keyboard-chewing{
	width:550px;
}

.z-keyboard-list-paginiation-state{
	display:none;
}
.z-keyboard-list-paginiation-pre,.z-keyboard-list-paginiation-next{
	margin-left:20px;
	display:none;
	cursor:pointer;
}

/* TODO FIX THE VIEW DIFFERENCE OF IE6 */
.z-keyboard-chewing .z-keyboard-key-11{
	margin-left:20px;
}
.z-keyboard-chewing .z-keyboard-key-21{
	margin-left:40px;
}
.z-keyboard-chewing .z-keyboard-key-31{
	margin-left:60px;
}
.z-keyboard-key-41{
	margin-left:50px;
}
.z-keyboard-key-42{
	width:260px;
}