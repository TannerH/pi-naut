package pi.naut.gpio.display.ssd1306.layout;

import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.trigger.GpioTrigger;
import net.fauxpark.oled.SSD1306;
import pi.naut.gpio.display.Layout;
import pi.naut.gpio.display.ssd1306.core.Action;
import pi.naut.gpio.display.ssd1306.core.component.wrapper.SelectableComponent;
import pi.naut.gpio.display.ssd1306.core.buffer.ComponentBuffer;
import pi.naut.gpio.listener.ActionListener;
import pi.naut.gpio.listener.ButtonListener;
import pi.naut.gpio.listener.ChangeLayoutListener;
import pi.naut.gpio.listener.DecrementActionListener;
import pi.naut.gpio.listener.DecrementItemListener;
import pi.naut.gpio.listener.IncrementActionListener;
import pi.naut.gpio.listener.IncrementItemListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pi.naut.gpio.config.PinConfiguration.*;

public class DefaultLayout implements Layout {

	private ComponentBuffer componentBuffer = new ComponentBuffer();

	private String title;

	private List<Action> actions;
	private int actionIndex = 0;

	private List<SelectableComponent> selectableComponents;
	private int itemIndex = 0;

	public DefaultLayout(String title, List<SelectableComponent> selectableComponents, List<Action> actions) {
		this.title = title;
		this.selectableComponents = selectableComponents;
		this.actions = actions;
	}

	@Override
	public void bufferLayoutTo(SSD1306 displayController) {
		componentBuffer.titleBar(displayController, title);
		componentBuffer.scrollableList(displayController, selectableComponents);
		componentBuffer.actionBar(displayController, actions);
	}

	@Override
	public Map<String, GpioPinListener> getListenerConfig() {
		Map<String, GpioPinListener> listenerMap = new HashMap<>();

		listenerMap.put(JOYSTICK_LEFT, new DecrementActionListener(this));
		listenerMap.put(JOYSTICK_RIGHT, new IncrementActionListener(this));
		listenerMap.put(JOYSTICK_CENTER, new ChangeLayoutListener(this));
		listenerMap.put(JOYSTICK_UP, new IncrementItemListener(this));
		listenerMap.put(JOYSTICK_DOWN, new DecrementItemListener(this));

		listenerMap.put(BUTTON_A, new ButtonListener());
		listenerMap.put(BUTTON_B, new ActionListener(this));

		return listenerMap;
	}

	@Override
	public Map<String, GpioTrigger> getTriggerConfig() {
		return new HashMap<>();
	}

	public List<Action> getActions() {
		return actions;
	}

	public List<SelectableComponent> getSelectableComponents() {
		return selectableComponents;
	}

	public String getTitle() {
		return title;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public void setSelectableComponents(List<SelectableComponent> selectableComponents) {
		this.selectableComponents = selectableComponents;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getActionIndex() {
		return actionIndex;
	}

	public void setActionIndex(int actionIndex) {
		this.actionIndex = actionIndex;
	}

	public int getItemIndex() {
		return itemIndex;
	}

	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
}
