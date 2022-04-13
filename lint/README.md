## MyBatisLint

**MyBatisLint** is a lint program for MyBatis Project to check the mapped statement especially.
It includes deep analysis methods to check type compatibility, existence of database objects and java parameter by connecting the database and using class files.

#### Project structure

It consists of two projects with Multi-Module Project with Maven.

- java-eager
- lint

### :triangular_flag_on_post: Feature List

|                                                               | Description                                                                             |              Status              |
|---------------------------------------------------------------|-----------------------------------------------------------------------------------------|:--------------------------------:|
| **:green_square: Key Feature**                                |                                                                                         |                                  |
| SQL Grammar Check                                             | Check if DDL, DML statements are correct grammatically                                  |        :heavy_check_mark:        |
| Object Existence Check                                        | Check if the column and table exists in the database <br>with the JDBC Connection       |        :heavy_check_mark:        |
| Property Existence Check                                      | Check if the java property exists in classes, source codes                              |        :heavy_check_mark:        |
| Type Compatibility Check                                      | Check type compatibility for every binary expression <br>consisting of Column and Value |        :heavy_check_mark:        |
| Diagnostics & Message                                         | Data structure for the trace log and meaningful message for the developer               |     :calendar: Soon (23 Aug)     |
| Command Line Interface<br>(Facade layer, Windows app package) | Facade API of Unit Test for CLI                                                         |     :calendar: Soon (27 Aug)     |
| **:green_square: Advanced Feature**                           |                                                                                         |                                  |
| Dynamic SQL                                                   | -                                                                                       | :calendar: Until the end of 2022 |
| Mapper Annotations<br>(@Select, @Update, ...)                 | -                                                                                       | :calendar: Until the end of 2022 |
| Auto download for JDK and tools                               | -                                                                                       | :calendar: Until the end of 2022 |
| IDE Plugins (vscode, IntelliJ)                                | -                                                                                       | :calendar: Until the end of 2023 |

:warning: This lint is designed based on MyBatis 3.5.9 and JDK 13. It will be tested and upgraded to support more versions of JDK and up-to-date MyBatis.

### References
- https://en.wikipedia.org/wiki/Prepared_statement
- https://dev.mysql.com/doc/refman/8.0/en/insert.html
- https://mybatis.org/mybatis-3/sqlmap-xml.html
- https://mybatis.org/spring/using-api.html