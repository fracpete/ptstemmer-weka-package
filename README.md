ptstemmer-weka-package
======================

Weka package for the PTStemmer (https://code.google.com/p/ptstemmer/), a stemmer
library for Portuguese.


Releases
--------

* [1.0.0](https://github.com/fracpete/ptstemmer-weka-package/releases/download/v1.0.0/ptstemmer-1.0.0.zip)


How to use packages
-------------------

For more information on how to install the package, see:

http://weka.wikispaces.com/How+do+I+use+the+package+manager%3F


Maven
-----

Add the following dependency in your `pom.xml` to include the package:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>ptstemmer-weka-package</artifactId>
      <version>1.0.0</version>
      <exclusions>
        <exclusion>
          <groupId>nz.ac.waikato.cms.weka</groupId>
          <artifactId>weka-dev</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```

