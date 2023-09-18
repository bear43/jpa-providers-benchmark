## JMH Benchmarks for top 3 JPA providers
* Hibernate
* Datanucleus
* EclipseLink

Idea is simple. 3 base models: `Student`, `Subject`, `Mark`.<br/>
`Student` can have marks for certain(s) subject.

Target measurement - get average ms/operation for reading all data in db for each table type.

`Student`, `Subject` - is a flat table.<br/>
`Mark` - is a join table.

Uses H2 in-memory database as a persistence storage.

AbstractBenchmark class contains constants such as:

| Constant         | Meaning                                                                    |
|------------------|----------------------------------------------------------------------------|
| TOTAL_STUDENTS   | Number of rows to insert into `Student` table                              |
| FULLNAME_LENGTH  | `Student.fullname` string value length (not database constraint, but code) |

`org.example.jpa.Setup` interface provides EntityManagerFactory from specific JPA provider.
`org.example.BenchmarkRunner` is an entry point for JMH.