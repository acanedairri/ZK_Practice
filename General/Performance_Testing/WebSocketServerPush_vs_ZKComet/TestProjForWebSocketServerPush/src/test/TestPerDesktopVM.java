package test;

import impl.serverpush.ServerPushUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

/** Tested with ZK 6.5.2
 * 
 * @author benbai123
 *
 */
public class TestPerDesktopVM {
	private String _counter = "positive";
	private String _inverseCounter = "negative";

	private AtomicInteger _cntCounter = new AtomicInteger(0);
	private Timer timer;
	private Desktop targetDesktop;

	public String getCounter () {
		return _counter;
	}
	public String getInverseCounter () {
		return _inverseCounter;
	}
	/**
	 * start server push with WebSocket for
	 * specific context "timerTask"
	 * 
	 * current desktop only
	 */
	@Command
	public void start () {
		targetDesktop = Executions.getCurrent().getDesktop();
		if (timer == null) {
			timer = new Timer();
			timer.schedule(getTimerTask(), 0, 10);
		}
	}

	/**
	 * update value to context 'positive' and 'negative' via
	 * WebSocket
	 * 
	 * All components that listen to these context will be updated
	 */
	@Command
	public void updateCounter () {
		int val = _cntCounter.incrementAndGet();
		push(val, "positive");
		push(-1*val, "negative");
	}
	// task to be scheduled to update context "timerTask" every second
	private TimerTask getTimerTask () {
		return new TimerTask() {
			public void run () {
				updateCounter();
			}
		};
	}
	/** push to all desktop
	 * 
	 * @param val
	 * @param context
	 */
	private void push (Object val, String context) {
		try {
			ServerPushUtil.pushVlaue(val, context, targetDesktop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
