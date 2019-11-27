
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoardListener implements KeyListener {
	private boolean[] keys = new boolean[120];

	public KeyBoardListener()
	{}

	public void keyPressed(KeyEvent Event) {
		keys[Event.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent Event) {
		keys[Event.getKeyCode()] = false;
	}

	public boolean up() {
		return keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
	}

	public boolean down() {
		return keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
	}

	public boolean left() {
		return keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
	}

	public boolean right() {
		return keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
	}

	public boolean shift() {
		return keys[KeyEvent.VK_SHIFT];
	}

	public boolean space() {
		return keys[KeyEvent.VK_SPACE];
	}

	public boolean r() {
		return keys[KeyEvent.VK_R];
	}

	public boolean q() {
		return keys[KeyEvent.VK_Q];
	}

	public boolean o() {
		return keys[KeyEvent.VK_O];
	}

	public boolean p() {
		return keys[KeyEvent.VK_P];
	}

	public void keyTyped(KeyEvent Event)
	{}
}
