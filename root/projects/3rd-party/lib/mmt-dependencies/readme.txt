These are the dependencies for the MMT Jar.  BouncyCastle is excluded.
Unfortunately truezip-swing seems to be required.


    <properties>
        <truezip.version>7.5.5</truezip.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>de.schlichtherle.truezip</groupId>
            <artifactId>truezip-file</artifactId>
            <version>${truezip.version}</version>
        </dependency>
        <dependency>
            <groupId>de.schlichtherle.truezip</groupId>
            <artifactId>truezip-driver-zip</artifactId>
            <version>${truezip.version}</version>
            <!--scope>runtime</scope-->
            <exclusions>
               <exclusion>
                  <artifactId>bcprov-jdk16</artifactId>
                  <groupId>org.bouncycastle</groupId>
               </exclusion>
            </exclusions>
        </dependency>