converterCSVToXML/gradlew -p converterCSVToXML run &&
cp converterCSVToXML/app/data/beans.xml springblog/src/main/resources/ &&
springblog/gradlew -p springblog bootJar