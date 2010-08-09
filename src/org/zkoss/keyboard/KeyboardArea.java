package org.zkoss.keyboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;

public class KeyboardArea extends Div {

	/**
	 *
	 */
	private static final long serialVersionUID = 182610786730070324L;
	private String _keyboard = "";

	public String getKeyboard() {
		return _keyboard;
	}

	public void setKeyboard(String _keyboard) {
		this._keyboard = _keyboard;
	}

	@Override
	public void onChildAdded(Component child) {

		child.addEventListener(Events.ON_FOCUS,new EventListener() {
			public void onEvent(Event event) throws Exception {
				Keyboard keyboard = (Keyboard) getFellow(_keyboard);
				if(keyboard != null){
					keyboard.setActivingComponent((Textbox)event.getTarget());
				}
			}
		});

		super.onChildAdded(child);
	}

}
