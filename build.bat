@echo off

call converterCSVToXML\gradlew -p converterCSVToXML run &&^
copy converterCSVToXML\app\data\beans.xml springblog\src\main\resources\ &&^
call springblog\gradlew -p springblog bootJar