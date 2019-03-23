package pi.naut.gpio.bonnet.input.listener;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import pi.naut.ApplicationState;
import pi.naut.gpio.bonnet.OLEDBonnet;

import javax.inject.Singleton;

@Singleton
public class DecrementItemListener implements GpioPinListenerDigital {


	private OLEDBonnet oledBonnet;
	private ApplicationState applicationState;

	public DecrementItemListener(OLEDBonnet oledBonnet, ApplicationState applicationState) {
		this.applicationState = applicationState;
		this.oledBonnet = oledBonnet;
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
		if (event.getState().isHigh()) {
			applicationState.getPullRequests().previous();
			oledBonnet.redisplayLayout();
		}
	}

}
