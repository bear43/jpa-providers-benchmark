## JMH Benchmarks for top 2 JPA providers
* Hibernate
* EclipseLink

The idea is simple. 3 base models: `Student`, `Subject`, `Mark`.<br/>
`Student` can have marks for certain(s) subject.

Target measurement - get average ms/operation for reading all data in db for each table type.

`Student`, `Subject` - is a flat table.<br/>
`Mark` - is a join table.

Uses 
* H2 in-memory database as a persistence storage.
* Spring boot as a glue between Hikari connection pool and DAO (and just cause that's really handy).

### ReadBenchmark

ReadBenchmark class contains constants such as:

| Constant         | Meaning                                                                    |
|------------------|----------------------------------------------------------------------------|
| TOTAL_STUDENTS   | Number of rows to insert into `Student` table                              |
| FULLNAME_LENGTH  | `Student.fullname` string value length (not database constraint, but code) |

ReadBenchmark measures reading capabilities from `Student` (a flat table) and `Mark` (a join table).
WriteBenchmark measures writing capabilities into the flat table and the join table.

The join table has each ManyToOne field in fetch mode LAZY to reach maximum performance. 
* EclipseLink doesn't have any static/dynamic weaving thus there is no laziness.
* Hibernate uses dynamic weaving with proxy mechanism aid.


**P.S**</br>
DataNucleus is disqualified due to runtime errors after enhancing