package org.example.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class Dao {
    StudentDao studentDao;
    SubjectDao subjectDao;
    MarkDao markDao;
    TransactionTemplate transactionTemplate;

    public Dao(ConfigurableApplicationContext context) {
        studentDao = context.getBean(StudentDao.class);
        subjectDao = context.getBean(SubjectDao.class);
        markDao = context.getBean(MarkDao.class);
        transactionTemplate = context.getBean(TransactionTemplate.class);
    }
}
