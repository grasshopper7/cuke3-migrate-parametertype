XStream in Java Cucumber implementation is dead. No more trying to decide whether to extend AbstractSingleValueConverter or implement Converter.

For the official announcement mentioning other goodies, wander to this link - https://cucumber.io/blog/2018/05/19/announcing-cucumber-jvm-3-0-0.

For the release notes navigate to - https://github.com/cucumber/cucumber-jvm/blob/master/CHANGELOG.md and scroll down to the 3.0.0-SNAPSHOT section. In this check out point 2 where annotations like @Delimiter, @Format, @Transformer,@XStreamConverter, @XStreamConverters are laid to rest. These must be replaced by a DataTableType or ParameterType.

For details on Cucumber Expressions which have been introduced to work alongside Regular Expressions refer to this - https://docs.cucumber.io/cucumber/cucumber-expressions/

Source Code – Have tried to add the relevant code portions in the article. For bigger source code will point to the relevant link.

Refer to [cuke2-parameter-datatable](https://github.com/grasshopper7/cuke2-parameter-datatable) link for Cucumber 2. Scenarios are contained in [parameter.feature](https://github.com/grasshopper7/cuke2-parameter-datatable/blob/master/cuke2-parameter-datatable/src/test/resources/features/parameter.feature). Step Definition in [ParameterStepDefinition.java](https://github.com/grasshopper7/cuke2-parameter-datatable/blob/master/cuke2-parameter-datatable/src/test/java/stepdef/ParameterStepDefinition.java). 

Refer to cuke3-migrate-parametertype link for Cucumber 3. Scenarios are contained in parametertype.feature. Step Definition code in ParameterTypeStepDefinition.java. Parameter registration code in Configurer.java.

What is ParameterType? - This contains all the mapping details and the transformation code for converting a string variable into a desired object.
Let us look at Cucumber 2 code –
Given the user name is John Doe  

@Given("^the user name is (.*?)$")
public void theUserNameIs(User user) {
	//XStream automatically converts name into a User object 
	//using the single argument string constructor
}

public class User {
	private String name;	
	public User(String name) {
		this.name = name;
	}
	//Getter setter methods
 }

Now if we use this same piece of code in Cucumber 3 we will get an error similar to this.
cucumber.runtime.CucumberException: Failed to invoke ………………., caused by java.lang.IllegalArgumentException: argument type mismatch
If we then use Cucumber Expression for the step definition pattern as below.
@Given("the user name is {user}")
public void theUserNameIs(User user) {
}
We get this error now.
cucumber.runtime.CucumberException: Could not create a cucumber expression for 'the user name is {user}'. 
It appears you did not register parameter type.

Let me guess, you are having buyers remorse and want XStream back. No worries. All that the error is saying, Cucumber needs some idea of how to convert from string to the object. And this will need to be registered.
Let us look at Cucumber 3 code by adding a ParameterType for conversion. The User dataobject remains the same.
public class Configurer implements TypeRegistryConfigurer {
	@Override
	public void configureTypeRegistry(TypeRegistry registry) {
		registry.defineParameterType(
			new ParameterType<>("user", ".*?", User.class, User::new));
	}
	@Override
	public Locale locale() {
		return Locale.ENGLISH;
	}
}

It is very important that the class Configurer needs to be placed inside the package structure mentioned in the glue option given inside @CucumberOptions. Registration of all ParameterType and DataTableType will happen inside the configureTypeRegistry method.
Let us look at ParameterType constructor in more detail.
ParameterType
"user",     -> Maps to the pattern mentioned in the stepdefinition expression {user}
".*?",      -> Regular expression for matching
User.class, -> Desired object class
User::new)  -> Transformation code, kind of similar to what is written in unmarshal() of custom XStream converter

This will output the same result as in the earlier cucumber versions.
With the ParameterType know how we can look at migrating from annotations like Delimiter, Format and Transform.
Migrating from Delimiter annotation - This was a convenient way to convert a delimited string into a list of strings or objects in Cucumber 2.
Given the user names are jane,john,colin,alice

@Given("^the user names are (.*?)$")
public void theUserNamesAre (@Delimiter(",") List<String> names) {
	//Get a list of Strings
}

@Given("^the users are (.*?)$")
public void theUserAre (@Delimiter(",") List<User> profs) {
	//Get a list of User objects. Any object with a single String argument 
	//constructor will be automatically converted by XStream.
}

In Cucumber 3, we need to register a ParameterType which maps to 'names'(from the stepdefinition method expression in Given annotation), takes a regular expression like '.*?', returns a List and a transformation method to return the same.
@Given("the user names are {names}")
public void givenUser(List<String> names) {
	//Returns a List of String
}

registry.defineParameterType(
	new ParameterType<>("names", ".*?", List.class, (String s) -> Arrays.asList(s.split(","))));

In case, we need to get a List of objects, say of a User, we can add the code in the transformation method. Simply register a new ParameterType which maps to {users}
@Given("the users are {users}")
public void givenProf(List<User> names) {
	//Returns a List of User
}

registry.defineParameterType(
	new ParameterType<>("users", ".*?", List.class, 
		(String s) -> Arrays.asList(s.split(",")).stream().map(User::new).collect(Collectors.toList())));

Migrating from Format annotation - This gave Cucumber 2 a hint about how to transform a String into an object such as a Date or a Calendar.
Given the date is 2012-03-01T06:54:12

@Given("^the date is (\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})$")
public void the_date_is(@Format("yyyy-MM-dd'T'HH:mm:ss") Date date) {
	//Returns a java.util.Date object
}

In Cucumber 3, we need to register a ParameterType which maps to 'names', takes a regular expression like '.*?', returns a java.time.LocalDateTime and a transformation method to return the same.
@Given("the date is {date_iso_local_date_time}")
public void the_date_is(LocalDateTime date) {
	//Return a java.time.LocalDateTime object
}

registry.defineParameterType(new ParameterType<>("date_iso_local_date_time", // 2011-12-03T10:15:30
	"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", LocalDateTime.class,
		(String s) -> LocalDateTime.parse(s)));

Migrating from Transformer annotation - Have a look at the first example of converting the string in the feature file to a User object. If there is a single argument constructor which takes a String, then XStream converts it automatically. All we need to do is add the parsing code to the specific constructor.
What happens when we do not have a single argument constructor or we do not have access to it? The way out is to use the Transformer annotation in the stepdefinition method.
Refer to FullNameTransformer and FullName dataobject
Given the name is 'John Mich Arthur Doe'

@Given("^the name is (.*?)$")
public void theAmountIs(@Transform(FullNameTransformer.class) FullName name) {
	System.out.println(name); //Get this output - FullName [firstName=John, title=Doe, middleName=Mich Arthur]
}

public class FullNameTransformer extends Transformer<FullName> {
	@Override
	public FullName transform(String inputName) {
		String[] names = inputName.split(" ");
		FullName fullName = new FullName();		
		if(names.length >= 1) fullName.setTitle(names[names.length - 1]);
		if(names.length >= 2) fullName.setFirstName(names[0]);
		if(names.length > 2)  fullName.setMiddleName(Arrays.stream(names, 1, names.length - 1).collect(Collectors.joining(" ")));	
		return fullName;
	}
}

public class FullName {
	private String firstName = "";
	private String title = "";
	private String middleName = "";
	public FullName() {	}
	//Plus getter setter	
}

In Cucumber 3, we just need to declare a ParameterType to hold this logic of converting string to a FullName object. The parsing and object creation code that was in the transformer moves to the ParameterType constructor. Or even move it to a class and refer to it by method reference.
Refer to FullName dataobject
@Given("the name is {fullname}")
public void theColorIs(FullName fullName) {
	//Returns the FullName object
}

registry.defineParameterType(
	new ParameterType<>("fullname", ".*?", FullName.class, FullName::parseNameDetails));
			
//In FullName class
public static FullName parseNameDetails(String name) {
	String[] names = name.split(" ");
	FullName fullName = new FullName();
	if (names.length >= 1) fullName.setTitle(names[names.length - 1]);
	if (names.length >= 2) fullName.setFirstName(names[0]);
	if (names.length > 2) fullName.setMiddleName(Arrays.stream(names, 1, names.length - 1).collect(Collectors.joining(" ")));
	return fullName;
}


What happens in the case of an enum? In Cucumber 2, XStream will automatically convert it into the desired enum. In Cucumber 3 a ParameterType has to be defined for this conversion.
public enum ProfLevels {  ASSISTANT, ASSOCIATE, PROFESSOR	}

Given the professor level is Associate

@Given("the professor level is {level}")
public void theProfessorLevelIs(ProfLevels level) {
	//Returns the ProfLevel enum
}

registry.defineParameterType(new ParameterType<>("level", ".*?", ProfLevels.class,
		(String s) -> ProfLevels.valueOf(s.toUpperCase())));


Migrating from XStream conversion - For the case similar to the situation in which a Transform annotation is used, one can also provide a custom XStream converter.
Refer to ProfessorXStreamConverter
Given the professor is John Doe

public class Professor {
	private String profName;
	public static Professor parseProfessor(String name) {
		Professor prof = new Professor();
		prof.setProfName(name);
		return prof;
	}
}

public class ProfessorXStreamConverter extends AbstractSingleValueConverter {
	@Override
	public boolean canConvert(Class cls) {
		return Professor.class.isAssignableFrom(cls);
	}
	@Override
	public Object fromString(String inputName) {			
		return Professor.parseProfessor(inputName);
	}
}

We need to inform Cucumber how to find this converter by using the @XStreamConverter or @XStreamConverters annotation. Declare using the singular version on the Professor class.
@XStreamConverter(value = ProfessorXStreamConverter.class)
public class Professor {

Or a global declaration placed on the runner class. In this you can have multiple converter declarations.
@XStreamConverters({
		@XStreamConverter(value = ProfessorXStreamConverter.class)
		//,@XStreamConverter(value = OtherXStreamConverter.class)  //Additional converters.
	})
@RunWith(Cucumber.class)

In Cucumber 3, the migration technique is similar to that for the Transform annotation, by declaring a ParameterType.
