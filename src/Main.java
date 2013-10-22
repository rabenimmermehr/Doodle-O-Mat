import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Main {

	private static WebDriver driver = null;
	
	private static String url = "http://doodle.com/aq5gv2gg498utgmcvd7in9ey/admin?#admin";
	private static String moreCellsText = "Weitere Zeitfelder hinzufügen";
	private static String copySlotsText = "Erste Zeile kopieren und einfügen";

	/**
	 * Takes the url of the doodle from the commmand line argument
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length  == 0){
			System.out.println("Please call this command like this: \"doodle-o-mat [url to the doodle]\"");
			System.exit(1);
		}
		
		// Start Chrome
		driver = new ChromeDriver();

		// Navigate to the Doodle Admin Page
		driver.get(args[0]);

		// Get the edit-button
		WebElement editBttn = driver.findElement(By.id("editPoll"));
		editBttn.click();

		// Now you are on the page where you can edit the name and stuff.
		// We want to keep everything the same, just continue to the next page
		WebElement continueBttn = driver.findElement(By.id("next1"));
		continueBttn.click();

		// Next up is the calendar view.
		// Wait for it to be fully loaded
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(By
				.className("delete")));

		// Delete all the selected days first.
		recursiveDeletion();

		//Get the current day
		Calendar cal = Calendar.getInstance();
		int day = cal.get(cal.DAY_OF_MONTH);

		// Sunday = 1, Saturday = 7
		int dayOfWeek = cal.get(cal.DAY_OF_WEEK);

		// In case it isn't Friday, we want to warn the user that he might have
		// screwed up
		if (dayOfWeek < 5) {
			System.out
					.println("It isn't friday yet, please make sure that the program behaved correctly");
		}

		//               Current Day
		//                 |    Days till Saturday
		//                 |       |              Add 2 for Monday
		int nextMonday = day + ((7 - dayOfWeek) + 2);
		System.out.println(7 - dayOfWeek);
		System.out.println(day);
		System.out.println(dayOfWeek);
		// nextMonday might potentially be bigger than the days of our month, so we need to check that
		System.out.println(cal.getMaximum(cal.DAY_OF_MONTH));
		nextMonday  =  nextMonday % (cal.getMaximum(cal.DAY_OF_MONTH));

		// Select the whole week
		recursiveSelection(nextMonday, 0);

		// Continue to the next page
		WebElement continueBttn2 = driver.findElement(By.id("next2a"));
		continueBttn2.click();

		// Now we can enter the times for each slot.
		// First of all, we need more options
		WebElement moreCells = driver
				.findElement(By.linkText(moreCellsText));
		moreCells.click();

		// Select the input fields and enter the time slots
		WebElement currentSlot = driver.findElement(By.id("do0_0"));
		currentSlot.sendKeys("10:00");

		currentSlot = driver.findElement(By.id("do0_1"));
		currentSlot.sendKeys("12:15");

		currentSlot = driver.findElement(By.id("do0_2"));
		currentSlot.sendKeys("13:45");

		currentSlot = driver.findElement(By.id("do0_3"));
		currentSlot.sendKeys("15:45");

		// Copy the first line into all the other days
		WebElement copySlots = driver
				.findElement(By.linkText(copySlotsText));
		copySlots.click();

		// Submit
		WebElement continueBttn3 = driver.findElement(By.id("finishEdit2b"));
		continueBttn3.click();

		// Wait for it to actually be submitted
		wait.until(ExpectedConditions.elementToBeClickable(By.id("toPoll")));

		// Close everything
		driver.quit();
		System.out.println("Everything worked, Good Bye!");
		System.exit(0);

	}

	public static void recursiveDeletion() {
		// Get the first element with the class "delete"
		WebElement deleteBttn = driver.findElement(By.className("delete"));

		// If there is one, click it and rerun this method. It will run, until
		// there is no other delete button
		try {
			deleteBttn.click();
			recursiveDeletion();
		} catch (Exception e) {
			System.out.println("Last Delete Button was clicked");
		}
	}

	/**
	 * Selects the next i days counting from @param nextMonday. If the following day is not in the current month, it automatically switches the month
	 * This needs to be recursively, since after every click, the elements
	 * become stale, so it has to be refreshed
	 * 
	 * @param nextMonday
	 *            day of the month of the next monday
	 * @param i
	 *            Offset from that monday
	 */
	public static void recursiveSelection(int nextMonday, int i) {
		// Get all the inputs on the page
		List<WebElement> dayBoxes = driver.findElements(By.xpath("//input"));

		//Check if the desired day is still in the month. If not, reset the counter accordingly
		Calendar cal = Calendar.getInstance();
		if (! (nextMonday + i <= cal.getMaximum(cal.DAY_OF_MONTH))) {
			//Set Monday to 0, because the calendar starts counting with the Monday from the previous month as a 0 and starts numerating from there on (top left corner of the grid)
			nextMonday = 0;
			
			// Click the button for the next month
			WebElement nextMonth = driver
					.findElement(By.className("nextMonth"));
			nextMonth.click();

			// Continue with the recursion, it will break at the desired point,
			// since the offset is the counter
			recursiveSelection(nextMonday, i);
			return;
		}

		for (WebElement dayBox : dayBoxes) {
			// Iterate over all the boxes. The boxes in the calendar have id's
			// like this:
			// "day<DayOfTheMonth>". Grab the monday + offset
			try {
				if (dayBox.getAttribute("id").equals("day" + (nextMonday + i))) {
					System.out.println("Monday: " + nextMonday + " offset: "
							+ i);
					// select that day
					dayBox.click();

					// If the offset hasn't reached friday yet, call this method
					// again with a higher offset, otherwise exit the method,
					// since
					// the elements on the website got stale
					if (i < 4) {
						recursiveSelection(nextMonday, i + 1);
					}
					return;
				}
			} catch (StaleElementReferenceException e) {
				//Somehow, I don't know how, if you switch the month, an element becomes stale, even though the list is refresehed. If you just rerun it
				//another time, the element isn't stale anymore
				System.out.println("Stale Element");
				recursiveSelection(nextMonday, i);
				return;
			}
		}

	}

}
