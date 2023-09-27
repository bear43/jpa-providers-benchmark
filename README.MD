## JMH Benchmarks for top 2 JPA providers
* Hibernate
* EclipseLink

The idea is simple. 3 base models: `Student`, `Subject`, `Mark`.<br/>
`Student` can have marks for certain(s) subject.

Target measurement - get average ms/operation for reading and writing data for each table type.

`Student`, `Subject` - is a flat table.<br/>
`Mark` - is a join table.

Uses 
* H2 in-memory database as a persistence storage.
* Spring boot as a glue between Hikari connection pool and DAO (and just cause that's really handy).

### ReadBenchmark

ReadBenchmark class contains constants such as:

| Constant        | Meaning                                                                    |
|-----------------|----------------------------------------------------------------------------|
| TOTAL_STUDENTS  | Number of rows to insert into `Student` table                              |
| TOTAL_SUBJECTS  | Number of rows to insert into `Subject` table                              | 
| FULLNAME_LENGTH | `Student.fullname` string value length (not database constraint, but code) |

ReadBenchmark measures reading capabilities from `Student` (a flat table) and `Mark` (a join table).
WriteBenchmark measures writing capabilities into the flat table and the join table.

The join table has each ManyToOne field in fetch mode LAZY to reach maximum performance. 
* EclipseLink doesn't have any static/dynamic weaving thus there is no laziness.
* Hibernate uses dynamic weaving with proxy mechanism aid.

TOTAL_SUBJECTS = 10;</br>
TOTAL_STUDENTS = 10_000;

### Hibernate

| Benchmark                                                 | Mode | Cnt | Score   | Error  | Units |
|-----------------------------------------------------------|------|-----|---------|--------|-------|
| ReadBenchmark.readAllFromFlatTable                        | avgt | 10  | 3.906   | ±0.005 | ms/op |
| ReadBenchmark.readAllFromFlatTableInTx                    | avgt | 10  | 5.532   | ±0.012 | ms/op |
| ReadBenchmark.readAllFromJoinTable                        | avgt | 10  | 8.930   | ±0.051 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTx                    | avgt | 10  | 12.274  | ±0.071 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTxAccessLazyGetters   | avgt | 10  | 12.333  | ±0.096 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTxAccessStudentGetter | avgt | 10  | 12.176  | ±0.095 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTxAccessSubjectGetter | avgt | 10  | 12.003  | ±0.102 | ms/op |
| WriteBenchmark.insertMarks                                | avgt | 10  | 259.680 | ±6.575 | ms/op |
| WriteBenchmark.insertMarksInTx                            | avgt | 10  | 242.926 | ±1.210 | ms/op |
| WriteBenchmark.insertStudents                             | avgt | 10  | 23.823  | ±0.095 | ms/op |
| WriteBenchmark.insertStudentsInTx                         | avgt | 10  | 22.426  | ±0.049 | ms/op |
| WriteBenchmark.insertSubjects                             | avgt | 10  | 0.022   | ±0.001 | ms/op |
| WriteBenchmark.insertSubjectsInTx                         | avgt | 10  | 0.023   | ±0.001 | ms/op |

### EclipseLink

| Benchmark                                                 | Mode | Cnt | Score   | Error  | Units |
|-----------------------------------------------------------|------|-----|---------|--------|-------|
| ReadBenchmark.readAllFromFlatTable                        | avgt | 10  | 16.230  | ±0.120 | ms/op |
| ReadBenchmark.readAllFromFlatTableInTx                    | avgt | 10  | 9.722   | ±0.078 | ms/op |
| ReadBenchmark.readAllFromJoinTable                        | avgt | 10  | 44.054  | ±0.715 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTx                    | avgt | 10  | 40.051  | ±0.067 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTxAccessLazyGetters   | avgt | 10  | 39.429  | ±0.130 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTxAccessStudentGetter | avgt | 10  | 39.688  | ±0.123 | ms/op |
| ReadBenchmark.readAllFromJoinTableInTxAccessSubjectGetter | avgt | 10  | 40.438  | ±0.126 | ms/op |
| WriteBenchmark.insertMarks                                | avgt | 10  | 140.745 | ±4.767 | ms/op |
| WriteBenchmark.insertMarksInTx                            | avgt | 10  | 116.641 | ±0.335 | ms/op |
| WriteBenchmark.insertStudents                             | avgt | 10  | 20.149  | ±0.074 | ms/op |
| WriteBenchmark.insertStudentsInTx                         | avgt | 10  | 15.306  | ±0.045 | ms/op |
| WriteBenchmark.insertSubjects                             | avgt | 10  | 0.015   | ±0.001 | ms/op |
| WriteBenchmark.insertSubjectsInTx                         | avgt | 10  | 0.015   | ±0.001 | ms/op |

### Benchmarks description

| Benchmark                                                 | Description                                                                                                        |
|-----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| ReadBenchmark.readAllFromFlatTable                        | Select data from `Student` table without transaction                                                               |
| ReadBenchmark.readAllFromFlatTableInTx                    | Select data from `Student` table within transaction                                                                |
| ReadBenchmark.readAllFromJoinTable                        | Select data from `Mark` table without transaction                                                                  |
| ReadBenchmark.readAllFromJoinTableInTx                    | Select data from `Mark` table within transaction                                                                   |
| ReadBenchmark.readAllFromJoinTableInTxAccessLazyGetters   | Select data from `Mark` table within transaction<br/>And invoke lazy getters (leads to additional select)          |
| ReadBenchmark.readAllFromJoinTableInTxAccessStudentGetter | Select data from `Mark` table within transaction<br/>And invoke `Student` lazy getter (leads to additional select) |
| ReadBenchmark.readAllFromJoinTableInTxAccessSubjectGetter | Select data from `Mark` table within transaction<br/>And invoke `Subject` lazy getter (leads to additional select) |
| WriteBenchmark.insertMarks                                | Insert data into `Mark` table without transaction                                                                  |
| WriteBenchmark.insertMarksInTx                            | Insert data into `Mark` table within transaction                                                                   |
| WriteBenchmark.insertStudents                             | Insert data into `Student` table without transaction                                                               |
| WriteBenchmark.insertStudentsInTx                         | Insert data into `Student` table within transaction                                                                |
| WriteBenchmark.insertSubjects                             | Insert data into `Subject` table without transaction                                                               |
| WriteBenchmark.insertSubjectsInTx                         | Insert data into `Subject` table within transaction                                                                |


**P.S**</br>
DataNucleus is disqualified due to runtime errors after enhancing