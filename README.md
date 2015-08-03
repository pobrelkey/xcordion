This is an import of the old xcordion Google Code [repository](http://code.google.com/p/xcordion).  It's unlikely to be updated in the future - but feel free to fork it as the basis of your own work.

(original README follows)

----

XCordion is the working title of an unofficial development branch of the open-source Concordion software specification and testing framework.  This is a work in progress, and not supported or sanctioned by the original authors of [Concordion](http://www.concordion.org).

Concordion is a testing tool similar in approach to the [FIT](http://fit.c2.com/) framework, but (we believe) easier to understand and use.  Non-technical team members write HTML documents containing plain-English acceptance test criteria.  This HTML is then instrumented with code and assertions at appropriate points in the document.  When the tests are run (usually by a JUnit test class), an output document is generated from the original document highlighting (through lots of pretty red/green colors) which assertions have passed and which have failed.

This Google Code project serves primarily as our public sandbox, as we develop changes which will be submitted back to the main Concordion maintainers at some point.  Our current goals include better table handling and the ability to run tests written for earlier, commercial releases of Concordion.  Future goals include support for other scripting engines, such as JRuby or Rhino.
