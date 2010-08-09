package org.zkoss.keyboard;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.keyboard.events.WebKeyEvent;
import org.zkoss.zk.au.out.AuAlert;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.api.InputElement;

public class Keyboard extends Div {

	/**
	 *
	 */
	private static final long serialVersionUID = -3864268102440848141L;
	private InputElement _activingComponent = null;

	private static HashMap<String, String> CHEWING_TABLE;

	static {
		CHEWING_TABLE = new HashMap<String, String>();
		CHEWING_TABLE.put("ㄅ", "1");
		CHEWING_TABLE.put("ㄉ", "2");
		CHEWING_TABLE.put("ˇ" , "3");
		CHEWING_TABLE.put("ˋ" , "4");
		CHEWING_TABLE.put("ㄓ", "5");
		CHEWING_TABLE.put("ˊ" , "6");
		CHEWING_TABLE.put("˙" , "7");
		CHEWING_TABLE.put("ㄚ", "8");
		CHEWING_TABLE.put("ㄞ", "9");
		CHEWING_TABLE.put("ㄢ", "0");
		CHEWING_TABLE.put("ㄆ", "q");
		CHEWING_TABLE.put("ㄊ", "w");
		CHEWING_TABLE.put("ㄍ", "e");
		CHEWING_TABLE.put("ㄐ", "r");
		CHEWING_TABLE.put("ㄔ", "t");
		CHEWING_TABLE.put("ㄗ", "y");
		CHEWING_TABLE.put("ㄧ", "u");
		CHEWING_TABLE.put("ㄛ", "i");
		CHEWING_TABLE.put("ㄟ", "o");
		CHEWING_TABLE.put("ㄣ", "p");
		CHEWING_TABLE.put("ㄦ", "-");
		CHEWING_TABLE.put("ㄇ", "a");
		CHEWING_TABLE.put("ㄋ", "s");
		CHEWING_TABLE.put("ㄎ", "d");
		CHEWING_TABLE.put("ㄑ", "f");
		CHEWING_TABLE.put("ㄕ", "g");
		CHEWING_TABLE.put("ㄘ", "h");
		CHEWING_TABLE.put("ㄨ", "j");
		CHEWING_TABLE.put("ㄜ", "k");
		CHEWING_TABLE.put("ㄠ", "l");
		CHEWING_TABLE.put("ㄤ", ";");
		CHEWING_TABLE.put("ㄈ", "z");
		CHEWING_TABLE.put("ㄌ", "x");
		CHEWING_TABLE.put("ㄏ", "c");
		CHEWING_TABLE.put("ㄒ", "v");
		CHEWING_TABLE.put("ㄖ", "b");
		CHEWING_TABLE.put("ㄙ" , "n");
		CHEWING_TABLE.put("ㄩ", "m");
		CHEWING_TABLE.put("ㄝ", ",");
		CHEWING_TABLE.put("ㄡ", ".");
		CHEWING_TABLE.put("ㄥ", "/");
		CHEWING_TABLE.put("space", " ");
	}

	static {
		addClientEvent(Keyboard.class, WebKeyEvent.NAME, CE_IMPORTANT | CE_NON_DEFERRABLE);
	}

	private String _type = "custom";

	public void setType(String type) {
		this._type = type;
	}

	public String getType() {
		return _type;
	}

	public InputElement getActivingComponent() {
		return _activingComponent;
	}

	public void setActivingComponent(Textbox activingComponent) {
		if (_activingComponent == null
				|| !_activingComponent.getUuid().equals(
						activingComponent.getUuid())) {
			this._activingComponent = activingComponent;
			this.smartUpdate("activingComponentId", activingComponent.getId());
		}
	}

	@Override
	public String getZclass() {
		return _zclass == null ? "z-keyboard" : _zclass;
	}

	@Override
	protected void renderProperties(ContentRenderer renderer)
			throws IOException {

		render(renderer, "type", _type);
		super.renderProperties(renderer);
	}

	public void onWebKeyPress(WebKeyEvent keyEvent) {

		if (_activingComponent != null) {
			if ("chewing".equals(_type)) {
				if (this.isListAction(keyEvent.getKey())) {
					doList(keyEvent.getSpells(), keyEvent.getKey());
				} else {
					doAddSpell(keyEvent.getSpells(), keyEvent.getKey());
				}
			}
		}

	}

	private void doAddSpell(List spells, String key) {

		// 確保不是沒辦法拼出字的無效組合

		if (isSpellCorrect(getQueryWord(spells, key))) {
			response(new AuInvoke(this, "appendSpell", key));
		} else {
			response(new AuInvoke(this, "errorSpell"));
		}
	}

	private void doList(List spells, String key) {
		ArrayList<String> list = this.getWordList(getQueryWord(spells, key));

		if (list.size() > 0) {
			response(new AuInvoke(this, "openList", new Object[] { key, list }));
		} else {
			response(new AuInvoke(this, "errorSpell"));
		}
	}

	/**
	 * Processes an AU request.
	 */
	@Override
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (WebKeyEvent.NAME.equals(cmd)) {
			WebKeyEvent evt = WebKeyEvent.getKeyEvent(request);
			Events.sendEvent(evt.getTarget(), evt);

		} else {
			super.service(request, everError);
		}
	}

	public String getQueryWord(List spells, String key) {
		StringBuffer queryword = new StringBuffer();
		for (Object spellObject : spells) {

			String spell = (String) spellObject;
			queryword.append(chewingToAsc(spell));
		}
		return queryword.toString() + chewingToAsc(key);
	}

	/**
	 * 判斷是否該列出清單
	 *
	 * @param word
	 *            到目前為止的輸入拼字全文
	 * @return
	 */
	public boolean isListAction(String word) {
		return chewingToAsc(word).matches(".*[3467 ]$");
	}

	public boolean isSpellCorrect(String query_word) {
		return getWordList(query_word).size() > 0;
	}

	/**
	 * 取得選字清單
	 */
	public ArrayList<String> getWordList(String query_word) {

		String[] word_array = readWordList(query_word);

		ArrayList<String> list = new ArrayList<String>();

		for (String word : word_array) {
			if (word.startsWith(query_word)) {
				// word #=> "spell word"
				list.add(word.split(" ")[1]);
			}
		}

		return list;

	}

	private String[] readWordList(String word_query) {
		int first_char = (int) word_query.charAt(0);
		try {
			InputStream is = new FileInputStream(Sessions.getCurrent()
					.getWebApp()
					.getRealPath("WEB-INF/classes/chewing/" + first_char));
			String content = readFile(is, "utf-8");
			return content.split("[\r\n]+");

		} catch (Exception ex) {
			ex.printStackTrace();
			return new String[]{};
		}
	}

	private String chewingToAsc(String input) {
		String ret = CHEWING_TABLE.get(input);
		if (ret == null)
			return input;
		return ret;
	}

	public static String readFile(InputStream inputstream, String charset) {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputstream, charset));

			StringBuffer result = new StringBuffer();
			String line = reader.readLine();
			while (line != null) {
				result.append(line + "\n");
				line = reader.readLine();
			}

			return result.toString();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}
