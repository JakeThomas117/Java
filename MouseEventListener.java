
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseEventListener implements MouseListener, MouseMotionListener {
	private boolean left;
	private int x = 0;
	private int y = 0;

	public MouseEventListener() {}

	public void mousePressed(MouseEvent Event) {
		if (Event.getButton() == 1) {
			left = true;
		}
	}

	public void mouseReleased(MouseEvent Event) {
		if (Event.getButton() == 1) {
			left = false;
		}
	}

	public void mouseMoved(MouseEvent Event) {
		x = Event.getX();
		y = Event.getY();
	}

	public void mouseEntered(MouseEvent Event) {
		x = Event.getX();
		y = Event.getY();
	}

	public boolean leftClick() {
		return left;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean withinRect(int x1, int y1, int w, int h) {
		if (x > x1 && x < x1 + w && y > y1 && y < y1 + h) {
			return true;
		} else {
			return false;
		}
	}

	public void mouseClicked(MouseEvent Event) {}

	public void mouseDragged(MouseEvent Event) {}

	public void mouseExited(MouseEvent Event) {}
}
