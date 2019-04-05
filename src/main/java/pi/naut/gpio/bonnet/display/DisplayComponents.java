package pi.naut.gpio.bonnet.display;

import io.micronaut.core.util.StringUtils;
import net.fauxpark.oled.SSD1306;
import net.fauxpark.oled.font.CodePage1252;
import pi.naut.gpio.controller.DisplayController;
import util.StateList;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static pi.naut.gpio.bonnet.display.DisplayConstants.*;

@Singleton
public class DisplayComponents {

	@Inject
	private DisplayController displayController;
	private SSD1306 controller;

	private static final int MAX_ITEMS_PER_PAGE = 5; // without action bar

	@PostConstruct
	private void init() {
		this.controller = displayController.getSsd1306();
	}

	public void startupScreen(String user) {
		controller.getGraphics().text(42, MIN_XY, new CodePage1252(), "PI-NAUT");
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream resourceAsStream = classLoader.getResourceAsStream("raspberry.png");
			BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(resourceAsStream));
			controller.getGraphics().image(bufferedImage, 48, 12, 32, 40);
		} catch (IOException e) {
			e.printStackTrace();
		}
		controller.getGraphics().text(21, MAX_Y - FONT_HEIGHT, new CodePage1252(), "WELCOME " + user.toUpperCase());
	}

	public void titleBar(String title) {
		if (StringUtils.isNotEmpty(title)) {
			controller.getGraphics().text(25, MIN_XY, new CodePage1252(), title);
			controller.getGraphics().line(MIN_XY, TEXT_HEIGHT, MAX_X, TEXT_HEIGHT);
		}
	}

	public void paginatedList(StateList list) {
		bufferList(list, MIN_XY, true);
	}

	public void scrollableList(StateList stateList) {
		bufferList(stateList, (RADIUS_SELECTED * 2) + (PADDING * 2), false);
		if (stateList.hasCurrent()) {
			controller.getGraphics().circle(RADIUS_SELECTED, (TEXT_HEIGHT + (PADDING * 2)) + (TEXT_HEIGHT * (stateList.currentIndex() % MAX_ITEMS_PER_PAGE)), 1);
		}
	}

	private void bufferList(StateList stateList, int xOffset, boolean paginated) {
		if (!stateList.hasCurrent()) {
			return;
		}
		int currentPage = stateList.currentIndex() > MAX_ITEMS_PER_PAGE ? stateList.currentIndex() / MAX_ITEMS_PER_PAGE : 1;
		int maxPages = stateList.getList().size() > MAX_ITEMS_PER_PAGE ? stateList.getList().size() / MAX_ITEMS_PER_PAGE : 1;
		int maxItems = stateList.getList().size() < MAX_ITEMS_PER_PAGE ? stateList.getList().size() : MAX_ITEMS_PER_PAGE;

		int indexOffset = paginated
				? (stateList.currentIndex() % maxPages) * MAX_ITEMS_PER_PAGE
				: (currentPage - 1) * MAX_ITEMS_PER_PAGE;

		if (currentPage != 1) {
			bufferUpArrow();
		}
		for (int i = 0; i < maxItems; i++) {
			controller.getGraphics().text(
					xOffset,
					TEXT_HEIGHT + (TEXT_HEIGHT * (i + 1)) + 1,
					new CodePage1252(),     // TODO, use a monospaced font
					stateList.getList().get(i + indexOffset).toString()
			);
		}
		if (maxPages > 1 && currentPage != maxPages) {
			bufferDownArrow();
		}
	}

	private void bufferDownArrow() {
		controller.getGraphics().line(HALF_WIDTH - ARROW_SLOPE, BASE_HEIGHT_ARROW_DOWN, HALF_WIDTH, BASE_HEIGHT_ARROW_DOWN + ARROW_SLOPE);
		controller.getGraphics().line(HALF_WIDTH, BASE_HEIGHT_ARROW_DOWN + ARROW_SLOPE, HALF_WIDTH + ARROW_SLOPE, BASE_HEIGHT_ARROW_DOWN);
	}

	private void bufferUpArrow() {
		controller.getGraphics().line(HALF_WIDTH - ARROW_SLOPE, BASE_HEIGHT_ARROW_UP + ARROW_SLOPE, HALF_WIDTH, BASE_HEIGHT_ARROW_UP);
		controller.getGraphics().line(HALF_WIDTH, BASE_HEIGHT_ARROW_UP, HALF_WIDTH + ARROW_SLOPE, BASE_HEIGHT_ARROW_UP + ARROW_SLOPE);
	}

// TODO, take another swing at this
//	public void actionBar(List<Action> actions) {
//		if (CollectionUtils.isNotEmpty(actions)) {
//			int i = 1;
//			int size = actions.size();
//			int buttonWidth = (MAX_WIDTH / size);
//
//			for (Action action : actions) {
//				// button box
//				controller.getGraphics().rectangle(
//						(buttonWidth * (i - 1)) + (i - 1),
//						MAX_HEIGHT - HEIGHT_BUTTON,
//						buttonWidth,
//						HEIGHT_BUTTON,
//						false
//				);
//
//				// button text
//				controller.getGraphics().text(
//						(buttonWidth * (i - 1)) + (i - 1) + PADDING,
//						MAX_HEIGHT - FONT_HEIGHT - PADDING,
//						new CodePage1252(),
//						action.getLongDescription()  // TODO, make this responsive
//				);
//
//				// selected indicator
//				if (action.isSelected()) {
//					controller.getGraphics().circle(
//							(buttonWidth * i) - (RADIUS_SELECTED * 2) - PADDING,
//							MAX_HEIGHT - (HEIGHT_BUTTON / 2) - 1,
//							RADIUS_SELECTED
//					);
//				}
//
//				i++;
//			}
//		}
//	}

}