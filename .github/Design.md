## Design

This document contains requirements and technologies
to implement the MyBatis static analysis tool.
It will be a Java program that has CLI.
We support [versions](Spec.md).

### XML Parsing

> ### The parser checks XML validation.
>
> It prints xml errors.

> ### The parser provides start LC and end LC where tags, attributes and texts starts and ends in XML file.
> LC = the line and column
> The Stax parser in Jaxp provides the location for only elements.
> So, we calculate locations for others. We need the access to XML Files with the with line, column
> **I think that one bug of Stax parser is to return wrong column number which is bigger than expected one 
> when data of Character Event ends with '<', '</'.**
> 
> - offset = the character count from the start of element
> - elementStartLC: It's provided from Stax Parser
> - elementEndLC: It's provided from Stax Parser
> - AttributeStartOffset: It's calculated from string search
> - lastNewLineOffsetBeforeAttributeStart:
    > by scanning element string
> - newLineCountBeforeAttributeStartInElementString:
    > by scanning element string
> - AttributeValueEndColumnOffset: It's calculated from string search
> - lastNewLineOffsetBeforeAttributeValueEnd:
    > by scanning element string
> - newLineCountBeforeAttributeValueEndInElementString:
    > by scanning element string
>
> ##### Attribute Start Location
>
>
> - startLineNumber = elementStartLineNumber
    > \+ newLineCountBeforeAttributeStartInElementString
> - startColumnNumber = AttributeStartOffset
    > \- lastNewLineOffsetBeforeAttributeStart
>
> ##### Attribute Value End Location
>
> - endLineNumber = elementStartLineNumber
    > \+ newLineCountBeforeAttributeValueEndInElementString
> - endColumnNumber = AttributeValueEndColumnOffset
    > \- lastNewLineOffsetBeforeAttributeValueEnd
>
> ##### Function
>
> - LC findLC(string, startLine, startColumn, offsetSource)
    > : calculate the location
>
>https://www.javarticles.com/2015/12/jaxp-stax-stream-reader-example.html

> ### Data Structures for the Mapper
>
> ##### RawXMLMapper
> The data structure represents XML structure and locations
> for elements, attrs, placeholders.
> - It has inheritance.
>
>
> ```java
> interface Location{
>   int getStartLine();
>   int getStartColumn();
>   String getFullString();
> }
> abstract class AbstractElement implements Location {
>   List<AbstractAttribute> attrs;
> } 
> abstract class AbstractAttribute implements Location { 
>   String key;
>   String value;
> } 
> abstract class AbstractPartialText implements Location {
>   String text;
> } 
> class RawPlaceHolder implements Location { } 
> class RawXMLMapper extends AbstractElement{
>   List<Child> children;
>   List<RawStatement> statements;
> }
> class RawStatement extends AbstractElement implements Child{
>   List<Child> children;
> }
> class RawIfElement extends AbstractElement implements Child{
>   List<Child> children;
> }
> class RawPartialText extends AbstractPartialText implements Child{
>   List<RawPlaceHolder> placeholderList;
> }
> ```
> ```java
> class RawTextElementGroup{
>   List<RawTextElement> elements;
> }
>```
>
> ```xml
> <mapper namespace="db.BlogMapper">
>   <insert   id =  "insertAuthor" parameterType="model.Author"
>           useGeneratedKeys="true"
>           keyProperty="id">
>       <![CDATA[글씨를 굵은 글씨로 강조한다. <b>...</b> 태그를 사용한다. <b> 대신 <strong>을 사용해도 된다.]]>
>       insert into Author(name)
>       values (#{name})
>   </insert>
>
>   <insert   id="insertBlog" parameterType="mo
>   del.Blog" useGeneratedKeys="true"
>           keyProperty="id">
>       insert into Blog (author_id, blog_name, description, create_time)
>       values (#{author.id}
>       , #{blogName}
>       , #{description}
>       <if text="createTime != null">, #{createTime}</if>
>       )
>   </insert>
> </mapper>
> ```
> ```
> mapper: {
>   stmt1: {
>     text: {
>       placeholder1
>     }
>   }
>   stmt2: {
>     text: {
>       placeholder1,
>       placeholder2,
>       placeholder3
>     },
>     ifElement: {
>       text: {
>         placeholder1
>       }
>     }
>   }
> }
> ```

### Convert Mapped Statement into String

> ### The evaluation of the Dynamic SQL 
>
>
> A newly generated RawXMLMapper is a sql statement that parser can parse
> by evaluating dynamic SQLs
> and it keeps all original locations.
> 
>
> Categories of the dynamic sql are
> "if", "choose", "when", "otherwise", "trim", "where", "set", "foreach"
>
> Evaluation proceeds with **Depth-First Search**
> - RawTextElementGroup can create many candidates.
> - Scan old RawXMLMapper.
> - Create old RawXMLMapper.
> - Append _RawTextElement_ nodes that created nodes in old RawXMLMapper
> 
> ##### if
> 1. set true and append new _RawTextElement_ that is its child. 
> new _RawTextElement_ has old _RawTextElement_ as parent.
> ```xml
> <if test="title != null">
>     AND title like #{title}
> </if>
> ```
> 
> ##### choose, when, otherwise
> append _RawTextElementGroup_ that has all possible _RawTextElement_ instead of RawChooseElement 
> new _RawTextElement_ has old _RawTextElement_ as parent.
> ```xml
> <choose>
>   <when test="title != null"> 
>     AND title like #{title}
>   </when>
>   <when test="author != null and author.name != null">
>     AND author_name like #{author.name}
>   </when>
>   <otherwise>
>     AND featured = 1
>   </otherwise>
> </choose>
> ``` 
> 
> 
> ##### where
> First, append 'where'.
> Second, append new _RawTextElement_ that's created from old _RawTextElement_ 
> where leading "and | or " is removed in a first _RawTextElement_.
> new _RawTextElement_ has old _RawTextElement_ as parent.
> ```xml
> <where>
>   <if test="state != null"> <!-- It will be removed --> 
>        state = #{state}
>   </if> <!-- It will be removed -->
> </where>
> ```
> ##### trim 
>
> 1. Append new _RawTextElement_ that's created from old following _RawTextElement_
> by appending "prefix" 
> 2. Handle "prefixOverrides" and "suffixOverrides"
> ```xml
> <trim prefix="WHERE" prefixOverrides="AND |OR ">
> ...
> </trim>
> ```
> ##### foreach
>
> 1.  append new _RawTextElement_ that has a value of "open" as a parent.
> 2.  append new _RawTextElement_ that has _RawTextElement_ in text.
> 3.  append new _RawTextElement_ that has a value of "close" as a parent.
> ```xml
>  <foreach item="item" index="index" collection="list"
>    open="ID in (" separator="," close=")" nullable="true">
>      #{item}
>  </foreach>
> ```

### Parse SQL Statement

- The parser accepts placeholders.
- The parser prints the syntax error and its range.

### Object Name Check

- AST contains locations for tables, columns
- It answers following questions. 
  - If the table exists? 
  - If the column exists?

> ### retrieving metadata from the database
> JDBC getMetadata() is useful.

### Property Name Check

- It answers following questions. 
    - If the property is valid?
    - If the type is valid?

> ### Read all types from Java project


### Type Incompatibility Check

- It checks type incompatibility between column and java type.

> ### Build the table for the mapping of column and java type.
> Based on standard docs (JDBC Document)


