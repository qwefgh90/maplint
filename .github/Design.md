## Design

The Goal is to design a MyBatis analysis tool as a Java CLI program.
We support [versions](Support.md).

### MyBatis XML Parsing


> #### XML validation 
> 
>https://stackoverflow.com/questions/4915422/get-line-number-from-xml-node-java
>https://stackoverflow.com/questions/2798376/is-there-a-way-to-parse-xml-via-sax-dom-with-line-numbers-available-per-node

> #### Provide **the line and column** where tags, attributes and texts starts and ends in XML file
>
>The Stax parser in Jaxp provides the location for elements, attributes, etc.
>https://www.javarticles.com/2015/12/jaxp-stax-stream-reader-example.html

### Parse Mapped Statement

