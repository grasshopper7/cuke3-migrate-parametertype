package stepdef;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import dataobject.*;

import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableCellTransformer;
import io.cucumber.datatable.TableEntryTransformer;
import io.cucumber.datatable.TableRowTransformer;
import io.cucumber.datatable.TableTransformer;

public class Configurer implements TypeRegistryConfigurer {

	@Override
	public void configureTypeRegistry(TypeRegistry registry) {

		registry.defineParameterType(
				new ParameterType<>("names", ".*?", List.class, (String s) -> Arrays.asList(s.split(","))));
		
		registry.defineParameterType(
				new ParameterType<>("users", ".*?", List.class, 
						(String s) -> Arrays.asList(s.split(",")).stream().map(User::new).collect(Collectors.toList())));

		registry.defineParameterType(new ParameterType<>("date_iso_local_date_time", // 2011-12-03T10:15:30
				"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}", LocalDateTime.class,
				(String s) -> LocalDateTime.parse(s)));

		registry.defineParameterType(
				new ParameterType<>("fullname", ".*?", FullName.class, FullName::parseNameDetails));
		
		registry.defineParameterType(
				new ParameterType<>("amount", ".*?", Money.class, Money::parseMoneyDetails));

		registry.defineParameterType(new ParameterType<>("user", ".*?", User.class, User::new));
		
		registry.defineParameterType(new ParameterType<>("professor", ".*?", Professor.class, Professor::parseProfessor));

		registry.defineParameterType(new ParameterType<>("proflevel", ".*?", ProfLevels.class, (String s) -> ProfLevels.valueOf(s)));
		
	}

	@Override
	public Locale locale() {
		return Locale.ENGLISH;
	}

}
