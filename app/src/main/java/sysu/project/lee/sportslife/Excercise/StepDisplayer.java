package sysu.project.lee.sportslife.Excercise;

import java.util.ArrayList;

public class StepDisplayer implements StepListener {
	private int mCount = 0;

	public StepDisplayer() {
		notifyListener();
	}

	public void setSteps(int steps) {
		mCount = steps;
		notifyListener();
	}

	public void onStep() {
		mCount++;
		notifyListener();
	}

	public void reloadSettings() {
		notifyListener();
	}

	public void passValue() {
	}

	// -----------------------------------------------------
	// Listener

	public interface Listener {
		public void stepsChanged(int value);

		public void passValue();
	}

	private ArrayList<Listener> mListeners = new ArrayList<Listener>();

	public void addListener(Listener l) {
		mListeners.add(l);
	}

	public void notifyListener() {
		for (Listener listener : mListeners) {
			listener.stepsChanged((int) mCount);
		}
	}
}
