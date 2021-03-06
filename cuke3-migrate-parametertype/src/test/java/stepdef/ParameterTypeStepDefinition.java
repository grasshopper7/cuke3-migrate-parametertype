package stepdef;

import java.time.LocalDateTime;
import java.util.List;

import cucumber.api.java.en.Given;
import dataobject.FullName;
import dataobject.Money;
import dataobject.ProfLevels;
import dataobject.Professor;
import dataobject.User;

public class ParameterTypeStepDefinition {

	//---DELMITER
	@Given("the user names are {names}")
	public void givenNames(List<String> names) {
		System.out.println(names);
		System.out.println("");
	}

	@Given("the users are {users}")
	public void givenUsers(List<User> names) {
		System.out.println(names);
		System.out.println("");
	}

	//---FORMAT
	// (\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})
	@Given("the date is {date_iso_local_date_time}")
	public void the_date_is(LocalDateTime date) {		
		System.out.format("Day %d Month %d Year %d Hour %d Minute %d Second %d \n",
				date.getDayOfMonth(),date.getMonthValue(),date.getYear(),
				date.getHour(),date.getMinute(),date.getSecond());
		System.out.println("");
	}
	
	//---TRNSFORM
	@Given("the name is {fullname}")
	public void theFullNameIs(FullName fullName) {
		System.out.println(fullName);
		System.out.println("");

	}
	
	@Given("the amount is {amount}")
	public void theAmountIs(Money money) {
		System.out.println(money);
		System.out.println("");

	}
	
	//---XSTREAM
	@Given("the user name is {user}")
	public void theUserNameIs(User user) {
		System.out.println(user);
		System.out.println("");

	}
	
	@Given("the professor is {professor}")
	public void theProfessorNameIs(Professor prof) {
		System.out.println(prof);
		System.out.println("");
	}
	
	@Given("the professor level is {proflevel}")
	public void theProfessorLevelIs(ProfLevels level) {
		System.out.println(level);
		System.out.println("");

	}

}