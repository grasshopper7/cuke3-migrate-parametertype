Refer here for a detailed explanation - https://grasshopper.tech/98/

XStream in Java Cucumber implementation is dead. No more trying to decide whether to extend AbstractSingleValueConverter or implement Converter.

For the official announcement mentioning other goodies, wander to this link - https://cucumber.io/blog/2018/05/19/announcing-cucumber-jvm-3-0-0.

For the release notes navigate to - https://github.com/cucumber/cucumber-jvm/blob/master/CHANGELOG.md and scroll down to the 3.0.0-SNAPSHOT section. In this check out point 2 where annotations like @Delimiter, @Format, @Transformer,@XStreamConverter, @XStreamConverters are laid to rest. These must be replaced by a DataTableType or ParameterType.

For details on Cucumber Expressions which have been introduced to work alongside Regular Expressions refer to this - https://docs.cucumber.io/cucumber/cucumber-expressions/
