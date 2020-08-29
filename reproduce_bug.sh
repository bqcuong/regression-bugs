#!/bin/bash

mvn -Dhttps.protocols=TLSv1.2 -Denforcer.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true -DskipITs=true -Drat.skip=true -Dlicense.skip=true -Dfindbugs.skip=true -Dgpg.skip=true -Dskip.npm=true -Dskip.gulp=true -Dskip.bower=true -Denforcer.skip=true -DskipTests install -pl languagetool-core -pl languagetool-language-modules/fr
mvn dependency:copy-dependencies -pl languagetool-core -pl languagetool-language-modules/fr
rm languagetool-language-modules/fr/target/dependency/languagetool-core-4.2-SNAPSHOT.jar
