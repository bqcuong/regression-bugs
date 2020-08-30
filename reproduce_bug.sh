#!/bin/bash

mvn -Dhttps.protocols=TLSv1.2 -Denforcer.skip=true -Dcheckstyle.skip=true -Dcobertura.skip=true -DskipITs=true -Drat.skip=true -Dlicense.skip=true -Dfindbugs.skip=true -Dgpg.skip=true -Dskip.npm=true -Dskip.gulp=true -Dskip.bower=true -Denforcer.skip=true -DskipTests clean install -pl core -pl plugins/junit -pl plugins/jfreechart
mvn dependency:copy-dependencies -pl core -pl plugins/jfreechart
