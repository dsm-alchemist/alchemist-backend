package com.alchemist.bianca.entity.task;

import com.alchemist.bianca.dto.task.request.TaskRequest;
import com.alchemist.bianca.dto.task.response.QTaskList;
import com.alchemist.bianca.dto.task.response.TaskList;
import com.alchemist.bianca.facade.UserFacade;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.alchemist.bianca.entity.task.QTask.task1;
import static com.alchemist.bianca.entity.user.QUser.*;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final UserFacade userFacade;

    @Override
    public List<TaskList> getTaskList(TaskRequest request) {
        return queryFactory
                .select(new QTaskList(
                        task1.task_id,
                        task1.task,
                        task1.isDone
                ))
                .from(task1)
                .join(task1.email, user)
                .where(
                        emailEq(request.getEmail()),
                        task1.date.eq(request.getDate())
                )
                .fetch();
    }

    @Override
    public void modifyTask(Long task_id, String task) {
        queryFactory
                .update(task1)
                .set(task1.task, task)
                .where(task1.task_id.eq(task_id))
                .execute();
    }

    @Override
    public void modifyDate(Long task_id, LocalDate date) {
        queryFactory
                .update(task1)
                .set(task1.date, date)
                .where(task1.task_id.eq(task_id))
                .execute();
    }

    @Override
    public void deleteTask(Long task_id) {
        queryFactory
                .delete(task1)
                .where(task1.task_id.eq(task_id))
                .execute();
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? user.email.eq(email) : user.email.eq(userFacade.getEmail());
    }
}