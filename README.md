# iri-md-doclet
Java Doclet for generating MD files from Javadoc

### Run
Clone project, then run with maven:
```maven clean compile package install```

Add plugin to the project (For now this looks for the IRI services/API class since thats the functions we publish)
```

By default, this outputs into ```target/site/```

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>${version.maven-javadoc-plugin}</version>
    <reportSets>
        <reportSet>
            <id>javadoc</id>
            <configuration>
                <doclet>org.iota.mddoclet.MDDoclet</doclet>
                <sourcepath>src/main/java</sourcepath>
                <useStandardDocletOptions>false</useStandardDocletOptions>
                <additionalOptions>
                    <additionalOption>-version "${project.version}"</additionalOption>
                    <additionalOption>-template "iota-java"</additionalOption>
                    <additionalOption>
                        -repolink "https://github.com/iotaledger/iota-java/blob/master/jota/src/main/java/"
                    </additionalOption>
                </additionalOptions>
                <quiet>true</quiet>
                <docletArtifact>
                    <groupId>com.github.iotaledger</groupId>
                    <artifactId>java-md-doclet</artifactId>
                    <version>2.2</version>
                </docletArtifact>
            </configuration>
            <reports>
                <report>javadoc</report>
            </reports>
        </reportSet>
    </reportSets>
</plugin>
```
